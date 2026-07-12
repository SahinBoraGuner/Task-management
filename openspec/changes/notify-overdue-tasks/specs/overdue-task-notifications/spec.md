## ADDED Requirements

### Requirement: Periodic overdue task check
The system SHALL check all tasks for overdue status every 10 seconds. A task is overdue when its `dueDate` is earlier than the current time and its `status` is not `COMPLETED` or `CANCELLED`.

#### Scenario: Cycle runs on schedule
- **WHEN** 10 seconds have elapsed since the previous check
- **THEN** the system queries all tasks and evaluates each non-terminal task's `dueDate` against the current time

#### Scenario: Completed and cancelled tasks are excluded
- **WHEN** a task's `status` is `COMPLETED` or `CANCELLED` and its `dueDate` is in the past
- **THEN** the task is not treated as overdue and no notification is sent for it

### Requirement: Overdue task notification logging
The system SHALL print a log entry for each task that is newly identified as overdue, in place of sending an email. Email delivery is out of scope for now and may be added in a future change.

#### Scenario: Overdue task triggers a log line
- **WHEN** a task is found to be overdue and has not previously been logged
- **THEN** the system prints a log line indicating the task is overdue, including the task title, due date, and the assigned user

#### Scenario: Notification is logged at most once per task
- **WHEN** a task has already had an overdue log line printed in a previous check cycle
- **THEN** the system does not print another overdue-notification log line for that task on subsequent cycles, even if the task remains overdue

### Requirement: Overdue check logging
The system SHALL print a log entry for each check cycle in addition to the per-task overdue notification log line.

#### Scenario: Cycle summary is logged
- **WHEN** a check cycle completes
- **THEN** a log line is printed indicating how many overdue tasks were found in that cycle
