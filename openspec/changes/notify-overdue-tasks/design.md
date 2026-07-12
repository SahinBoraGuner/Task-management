## Context

Task-Management is a Spring Boot 4 app (Java 25) with a Postgres-backed JPA layer (`Task`, `User` entities), no scheduling configured yet. `Task` has `dueDate` (`java.util.Date`), `status` (`TaskStatus` enum: `PENDING`, `COMPLETED`, `CANCELLED`, `IN_PROGRESS`, `READY`), and a `User` (which now has `email`). `TaskService`/`TaskRepo` already follow a simple Spring Data JPA + `@Slf4j` pattern that this change should stay consistent with.

## Goals / Non-Goals

**Goals:**
- Detect overdue tasks (`dueDate` < now, status not terminal) on a fixed 10-second cycle.
- Print exactly one overdue-notification log line per task (not one per cycle), instead of sending an email.
- Log every check cycle (how many overdue tasks found).

**Non-Goals:**
- No email sending in this change. Sending an actual email to the assigned user is deferred to a future change; a log line stands in for it now, using the same `User.email` field for reference in the log line.
- No user-facing configuration of notification preferences, digest emails, or reminder cadence (single overdue log line per task only).
- No change to `TaskController`/REST API surface — this is a background process only.
- No admin UI for viewing notification history.

## Decisions

**Scheduling: Spring's `@Scheduled(fixedRate = 10000)` via `@EnableScheduling`.**
Simplest option that matches "every 10 seconds" exactly, no new infrastructure (no Quartz, no external cron). Alternative considered: Quartz — rejected as overkill for a single fixed-interval job with no persistence/clustering requirements.

**Overdue definition: `dueDate < now() AND status NOT IN (COMPLETED, CANCELLED)`.**
`IN_PROGRESS`, `READY`, and `PENDING` are all "not done," so a task in any of those states past its due date counts as overdue. Alternative considered: only `PENDING` counts as overdue — rejected because `IN_PROGRESS`/`READY` tasks can equally miss their due date.

**Duplicate-notification prevention: add `overdueNotifiedAt` (nullable `Timestamp`) to `Task`.**
The query for "overdue tasks needing notification" becomes `dueDate < now AND status NOT IN (...) AND overdueNotifiedAt IS NULL`. After the log line is printed, the field is stamped and the task is never picked up again. Alternative considered: an in-memory `Set<Integer>` of notified task IDs in the scheduler bean — rejected because it resets on restart (causing duplicate log lines after every redeploy) and doesn't survive multiple app instances. Kept named `overdueNotifiedAt` (rather than `overdueLoggedAt`) so no rename is needed when email sending is added later.

**Notification mechanism: log line only, no email.**
Per explicit scope decision, `spring-boot-starter-mail`/`JavaMailSender`/SMTP configuration are not introduced in this change. The per-task log line includes the same information an email would (task title, due date, assigned user) so the future email version is a drop-in replacement of this one decision, not a redesign.

**Logging: reuse `@Slf4j` (already used in `TaskService`), one line per cycle summary and one line per newly-overdue task.**
Consistent with existing code style; no new logging framework/config needed.

## Risks / Trade-offs

- [Every-10-second full table scan of tasks] → Acceptable at current expected scale (small task-management app); if the table grows large, the indexed `overdueNotifiedAt IS NULL` + `dueDate` query keeps the scan cheap. Revisit with pagination/indexing if task volume grows significantly.
- [A log line is easy to miss compared to an email, so this change doesn't yet deliver an actionable notification to the user] → Accepted for this iteration per explicit scope decision; email sending can be layered on top of the same dedup/query logic in a future change without rework.

## Migration Plan

1. Add nullable `overdueNotifiedAt` column to `Task` (handled automatically by `spring.jpa.hibernate.ddl-auto=update` in this project's current dev setup).
2. Add `@EnableScheduling` to `TaskManagementApplication`.
3. Add the scheduled component and repository query.
4. No rollback complexity beyond removing the scheduled bean/config — the new column is additive and nullable, safe to leave in place even if the feature is later disabled.

## Open Questions

- When email sending is added in a future change, should the existing `overdueNotifiedAt` stamp (set by the logging-only version) be reused as-is, or should a separate flag distinguish "logged" from "emailed"? Not a blocker for this change.
