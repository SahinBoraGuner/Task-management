# SPDD Analysis: Overdue Task Check Scheduler

## Original Business Requirement

Add a system that checks for overdue tasks every 10 seconds. It should send an email and add a log line, but for now, do not send the email—only add the log line.

## Domain Concept Identification

#### Existing Concepts (from codebase)
- **Task** (`entity/Task.java`): The business entity being evaluated for overdue status. Has `dueDate` (`java.util.Date`, non-nullable), `status` (`TaskStatus` enum), and a `ManyToOne` relationship to `User`.
- **TaskStatus** (`entity/TaskStatus.java`): Enum with values `PENDING`, `COMPLETED`, `CANCELLED`, `IN_PROGRESS`, `READY`. No `OVERDUE` value currently exists — overdue-ness is not currently modeled as state, it must be derived.
- **User** (`entity/User.java`): Owner of a `Task`, already has an `email` field populated — this is the natural target for the (currently deferred) email notification.
- **TaskRepo** (`repository/TaskRepo.java`): `JpaRepository<Task, Integer>` with two custom `@Query` finder methods (by title, by description via `LIKE`). No existing query filters by `dueDate` or `status`.
- **TaskService** / **AdminService**: Both use Lombok's `@Slf4j` and `log.info(...)` calls for CRUD operations — this is the established logging convention in the codebase.

#### New Concepts Required
- **Overdue determination logic**: A rule that classifies a `Task` as "overdue" based on `dueDate` and `status`, evaluated at check time — relates to `Task` and `TaskStatus`.
- **Scheduled check process**: A periodic (10-second interval) trigger that repeatedly runs the overdue determination logic — a new architectural concern; no scheduling infrastructure (`@EnableScheduling`/`@Scheduled`) exists anywhere in the codebase today.
- **Notification step (deferred)**: A conceptual "notify overdue task" action that will eventually send an email to `User.email`, but for this iteration only produces a log line — relates to `Task` and `User`.
- **Already-notified tracking state**: A per-`Task` marker recording whether the overdue notification (log line now, email later) has already fired for the task's current overdue occurrence — introduced in this iteration per explicit user decision. This requires new persisted state on `Task` (the codebase has no existing concept of "notified" or "acknowledged" state to reuse). Relates directly to `Task`; scoped by the scheduled check process on each run.

#### Key Business Rules
- **10-second check cadence**: Explicit in the requirement — governs the scheduled check process.
- **Overdue tasks trigger two actions**: an email (deferred/not implemented yet) and a log line (must be implemented now) — governs the notification step.
- **Email suppression is temporary and explicit**: the requirement explicitly instructs that email sending is out of scope "for now," implying it is expected to be added later — governs how the notification step should be structured/extensible.
- **Implicit rule (needs surfacing)**: what counts as "overdue" — presumably `dueDate` in the past — is not explicitly tied to `TaskStatus` in the requirement. Whether `COMPLETED`/`CANCELLED` tasks should be excluded is not stated and governs the `Task`/`TaskStatus` relationship.
- **Notify-once rule (explicit, per user decision)**: each overdue task must trigger its notification (log line now, email later) only once per overdue occurrence — not repeatedly on every 10-second cycle. This governs both the `Task`/already-notified-tracking relationship and the scheduled check process's query/write behavior.

## Strategic Approach

#### Solution Direction
- Introduce a dedicated scheduled component that runs every 10 seconds using Spring's native scheduling support (`@EnableScheduling` + `@Scheduled`), since no scheduling infrastructure currently exists in the project and the stack (Spring Boot 4.0.1) supports this out of the box with no new dependency.
- The scheduled component queries for overdue tasks that have **not yet been notified** (a new repository query on `TaskRepo`, following the existing `@Query` convention already used for title/description search), then for each such task performs two conceptual steps: an email notification step (stubbed/no-op for this iteration) and a log line (using the same `@Slf4j` logging convention already established in `TaskService` and `AdminService`) — and finally marks the task as notified so it is excluded from subsequent runs.
- Data flow direction: Scheduled trigger → repository query for overdue-and-not-yet-notified tasks → per-task iteration → log line (email step deferred) → mark task as notified (persisted write).
- **Note on direction change from prior analysis**: the previously recommended "read-only, no side effects" detection approach is superseded by this decision — already-notified tracking inherently requires the scheduled process to write to `Task` rows.

#### Key Design Decisions
- **Where the scheduled check lives**: a new dedicated component vs. adding a `@Scheduled` method into existing `TaskService`. Trade-off: `TaskService`/`AdminService` are currently pure CRUD/query services with no cross-cutting or time-driven concerns; folding scheduling logic into them would mix responsibilities. → **Recommendation**: introduce a new, separate component dedicated to the overdue check, keeping CRUD services untouched.
- **How "overdue" is determined**: still derived at query time (based on `dueDate` vs. current time, filtered by non-terminal `TaskStatus`) rather than introducing a persisted `OVERDUE` status value — this part of the original recommendation still holds. → **Recommendation unchanged**: no `TaskStatus` enum change for this iteration; "overdue" remains a computed condition, not a status value.
- **How already-notified state is tracked** (new decision, driven by user request): in-memory tracking (e.g., a `Set<Integer>` of task IDs held inside the scheduler component) vs. a persisted field on `Task` (e.g., a new nullable column such as `overdueNotifiedAt` or `overdueNotified`). Trade-off: in-memory tracking is simpler and requires no schema change, but is lost on every application restart (previously-notified tasks would be re-logged once after a restart) and would not be correct if the app ever ran with multiple instances; a persisted field survives restarts and matches this project's JPA/Postgres-centric architecture, at the cost of the scheduler now writing to `Task` rows (a real, if narrow, schema change) and needing `spring.jpa.hibernate.ddl-auto=update` to pick up the new column automatically, as it already does for schema evolution in this project. → **Recommendation**: use a persisted field on `Task` (e.g., `overdueNotifiedAt: Date`, nullable, set the first time the task is detected overdue) — correctness across restarts matters more than avoiding a one-column schema addition, and `ddl-auto=update` already makes this low-friction in this project.
- **Shape of the deferred email step**: build a full notification abstraction (interface + no-op implementation) now vs. a minimal, clearly-marked placeholder. Trade-off: the project has no mail dependency (`spring-boot-starter-mail` is not in `pom.xml`) and no prior notification abstraction to extend, so building a full interface now is speculative. → **Recommendation**: keep the email step minimal and clearly marked as not-yet-implemented, deferring abstraction design to when real email requirements (template, recipient rules, retry behavior) are known.
- **Scheduling mechanism**: Spring's built-in `@Scheduled` (fixedRate/fixedDelay) vs. an external scheduler (e.g., Quartz). Trade-off: no Quartz or messaging dependency exists in `pom.xml`, and the requirement (single fixed 10-second interval, single application instance implied) doesn't need Quartz's persistence/clustering features. → **Recommendation**: use Spring's native `@Scheduled` support.
- **Whether a "re-notify" trigger is needed**: if a previously-notified task's `dueDate` is later pushed forward (via `updateTask`), should its notified flag reset so it can be re-flagged if it becomes overdue again? Not addressed by the current requirement — see Risk & Gap Analysis.

#### Alternatives Considered
- **Database-level triggers/stored procedures for overdue detection**: rejected — inconsistent with the project's Spring Data JPA-centric architecture; no existing DB-side logic in the codebase.
- **Message-queue-driven reminder system**: rejected — no message broker is present in the stack, and the requirement's scope (simple periodic check) doesn't justify the added infrastructure.
- **Modeling "overdue" as a new `TaskStatus` enum value**: considered but rejected for this iteration in favor of a derived overdue check plus a separate notified-tracking field (see Key Design Decisions above), to avoid conflating "is overdue" (a computed condition) with "has been notified" (a persisted fact) and to avoid transition-ownership questions not addressed by the requirement.
- **In-memory already-notified tracking**: considered but rejected in favor of a persisted field on `Task` (see Key Design Decisions above) — an in-memory set would silently lose its tracking state on every application restart.

## Risk & Gap Analysis

#### Requirement Ambiguities
- **Status exclusion**: whether tasks with `COMPLETED` or `CANCELLED` status should be excluded from the overdue check is not stated — a completed task with a past due date is arguably not "overdue" in a business sense.
- ~~**Repeat behavior**~~ — **Resolved by user decision**: an overdue task must be logged (and eventually emailed) only once per overdue occurrence, not on every 10-second cycle. This is now tracked via an already-notified marker on `Task` (see New Concepts Required and Strategic Approach).
- **Log line granularity**: unclear whether one log line should be written per overdue task found, or a single summary line per scheduled run (e.g., "3 overdue tasks found").
- **Email recipient**: not explicitly stated, though `User.email` on the task's associated `User` is the only plausible existing target.
- **Time reference**: `dueDate` is stored as `java.util.Date` with no explicit time zone handling anywhere in the codebase; the requirement doesn't clarify what "now" means relative to stored due dates (server local time is the only available reference today).
- **Notified-flag reset conditions** (new, introduced by already-notified tracking): the requirement doesn't say whether the notified marker should reset if a task's `dueDate` is later extended past the current time (via `updateTask`), or if its `status` changes back from a terminal state. Left unresolved for this iteration — flagged for confirmation before/during implementation.

#### Edge Cases
- **Tasks with no associated user**: `Task.user` is a `@ManyToOne` without `nullable = false`, so a task could exist without a linked `User` — relevant once the email step is actually implemented (no email address to send to).
- **Overlapping scheduler runs**: if the overdue query/logging work ever takes longer than 10 seconds (e.g., large task volume), Spring's default single-threaded scheduler could queue/delay runs rather than run concurrently — worth confirming expected behavior as task volume grows.
- **Tasks already overdue at startup**: since checks are time-driven, any task already overdue (and not yet marked notified) when the application starts will simply be picked up on the first scheduled run — likely desired, but not explicitly confirmed by the requirement.
- **Task edited after being marked notified**: if a task's `dueDate` is pushed forward via `updateTask` after it was already marked notified, the task would (with the currently planned design) stay permanently excluded from future overdue checks even if it becomes overdue again later — see the unresolved "Notified-flag reset conditions" ambiguity above.

#### Technical Risks
- **No scheduling infrastructure exists yet**: `@EnableScheduling` is not present anywhere in the codebase (confirmed via search) — this is genuinely greenfield and must be wired into the application bootstrap.
- **No mail dependency**: `spring-boot-starter-mail` is absent from `pom.xml` — not a blocker for this iteration since email sending is explicitly deferred, but flagged so it isn't forgotten when email is implemented later.
- **Schema change now required**: tracking already-notified state requires a new persisted field on `Task` (e.g., `overdueNotifiedAt`). `spring.jpa.hibernate.ddl-auto=update` will add the column automatically on next startup, consistent with how this project already manages schema evolution, but this is a genuine (if small) schema change that the prior iteration of this analysis explicitly avoided — called out here since it reverses that earlier recommendation.
- **`java.util.Date` usage**: the existing codebase uses `java.util.Date` (not `java.time.*`) for `dueDate`; the overdue comparison logic must work correctly with this existing type rather than introducing a new date/time convention inconsistent with the rest of the codebase.

#### Acceptance Criteria Coverage
| AC# | Description | Addressable? | Gaps/Notes |
|-----|-------------|--------------|------------|
| 1 | System checks for overdue tasks every 10 seconds | Yes | Addressable via Spring's native `@Scheduled` support; requires enabling scheduling in the application, which doesn't exist yet. |
| 2 | System sends an email for overdue tasks | Partial (intentionally deferred) | Requirement explicitly states email must NOT be sent yet — this AC is intentionally out of scope for this iteration, not a gap. No mail dependency exists yet, which will need to be added when this is implemented. |
| 3 | System adds a log line for overdue tasks | Yes | Addressable via the existing `@Slf4j` logging convention already used in `TaskService`/`AdminService`. Now scoped to fire once per overdue occurrence via already-notified tracking (per user decision), not on every 10-second cycle. Exact granularity (per-task vs. per-run) still needs clarification — see Requirement Ambiguities. |
