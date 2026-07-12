## Why

Tasks can currently sit past their due date indefinitely with no signal to the assigned user or anyone else. There is no background process in the application at all today (no scheduling is enabled), so overdue tasks are silently ignored unless a user happens to check the task list. Users need to be notified automatically when their tasks become overdue so they can act on them.

## What Changes

- Add a scheduled job that runs every 10 seconds and checks all tasks for overdue status (`dueDate` in the past and `status` not already `COMPLETED`/`CANCELLED`).
- For each overdue task found, print a log line noting the task is overdue (including title, due date, and assigned user). **Email sending is deferred** — a log line is printed instead of an email for now, and email delivery can be added in a later change.
- Print a log line for each check cycle summarizing how many overdue tasks were found, using the existing SLF4J logging already used in `TaskService`.
- Track which tasks have already been logged as overdue so the same task doesn't trigger a duplicate log line on every 10-second cycle.
- Add scheduling support (`@EnableScheduling`) to the application, since it doesn't exist in the project yet.

## Capabilities

### New Capabilities
- `overdue-task-notifications`: Periodic detection of overdue tasks, one-time overdue log line per task, and logging of each check cycle.

### Modified Capabilities
- (none — no existing specs in this project yet)

## Impact

- **New code**: a scheduler component (e.g. `OverdueTaskNotifier`); a way to mark/track already-logged tasks (new nullable timestamp column on `Task`, e.g. `overdueNotifiedAt`), plus a repository query to find overdue+not-yet-logged tasks.
- **Modified code**: `Task` entity (new tracking field), `TaskRepo` (new query for overdue tasks), `TaskManagementApplication` (enable scheduling).
- **Dependencies**: none new — no mail dependency or SMTP configuration is needed for this change.
- **Data**: existing `tasks` table gains a nullable notification-tracking column (`ddl-auto=update` will apply this automatically in dev, as already relied on for other columns).
