## 1. Configuration

- [x] 1.1 Add `@EnableScheduling` to `TaskManagementApplication`

## 2. Data model

- [x] 2.1 Add nullable `overdueNotifiedAt` (`Timestamp`/`Instant`) field to `Task` entity
- [x] 2.2 Add a `TaskRepo` query to find overdue, not-yet-notified tasks: `dueDate < now`, `status NOT IN (COMPLETED, CANCELLED)`, `overdueNotifiedAt IS NULL`

## 3. Scheduled check

- [x] 3.1 Add a scheduled component (e.g. `OverdueTaskNotifier`) with a `@Scheduled(fixedRate = 10000)` method
- [x] 3.2 In the scheduled method: fetch overdue/not-yet-notified tasks via the repo query, print a log line per task (task id/title, due date, assigned user's email), then stamp `overdueNotifiedAt` and persist via `TaskRepo`
- [x] 3.3 Log a cycle-summary line (count of overdue tasks found) on every run

## 4. Verification

- [ ] 4.1 Manually verify: create a task with a past `dueDate` and non-terminal status, confirm a cycle-summary log line and a per-task overdue log line are printed within ~10s
- [ ] 4.2 Manually verify: confirm the same task does not trigger a second overdue log line on the next cycle
- [ ] 4.3 Manually verify: set a task's `dueDate` in the future or `status` to `COMPLETED`/`CANCELLED`, confirm no overdue log line is printed for it
