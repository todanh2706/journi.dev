## Purpose
Describe the currently implemented streak and reminder-tracking behavior, including the public API shape and the underlying persistence expectations.

## Requirements

### Requirement: Heatmap Streak API
The backend SHALL expose heatmap streak tracking at `/api/v1/heatmap-streaks`. The API SHALL support listing all streak records, creating a streak for a user, retrieving a streak by owner user UUID, and retrieving a streak directly by streak UUID.

#### Scenario: Creating a heatmap streak
- **WHEN** a client posts `currentStreak` and `longestStreak` to `POST /api/v1/heatmap-streaks/{userId}`
- **THEN** the backend creates a streak record associated with that user

#### Scenario: Looking up a streak by owner
- **WHEN** a client requests `GET /api/v1/heatmap-streaks/{userId}` for a user with a streak record
- **THEN** the backend returns the corresponding `HeatmapStreakResponse`

### Requirement: No-Content Behavior for Empty Streak Queries
The streak endpoints SHALL distinguish between populated and empty collections. Collection and lookup requests SHALL return `204 No Content` when no streak data exists for the requested scope.

#### Scenario: Listing streaks in an empty database
- **WHEN** a client requests `GET /api/v1/heatmap-streaks` and no streak rows exist
- **THEN** the backend returns `204 No Content`

### Requirement: Reminder Notification API
The backend SHALL expose reminder notification management at `/api/v1/reminder-notifications`. The API SHALL support listing all notifications, listing notifications for a single receiver, fetching an individual notification by UUID, and creating a notification for a specific user.

#### Scenario: Creating a reminder notification
- **WHEN** a client posts notification type, channel, message, and status to `POST /api/v1/reminder-notifications/{userId}`
- **THEN** the backend persists a notification linked to that receiver and returns the created notification DTO

#### Scenario: Listing notifications for one user
- **WHEN** a client requests `GET /api/v1/reminder-notifications/{userId}` and notifications exist for that receiver
- **THEN** the backend returns the receiver-scoped notification list

### Requirement: Reminder Scheduling Metadata
Reminder notification records SHALL carry scheduling and delivery timestamps in the persistence model. The current creation flow SHALL populate receiver, type, channel, message, and status directly, while leaving scheduling and sent-time fields available for later workflow expansion.

#### Scenario: Reviewing the notification entity and service pair
- **WHEN** a contributor compares `ReminderNotification` with `ReminderNotificationService`
- **THEN** they can see that scheduling metadata exists in the model even though the current create endpoint does not set those fields
