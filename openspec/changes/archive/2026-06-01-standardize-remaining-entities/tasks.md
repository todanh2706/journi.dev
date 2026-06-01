## 1. MapStruct Mappers Implementation

- [x] 1.1 Create `LearningRoadmapMapper` for `LearningRoadmap` entity
- [x] 1.2 Create `SkillNodeMapper` for `SkillNode` entity
- [x] 1.3 Create `NodePrerequisiteMapper` for `NodePrerequisite` entity
- [x] 1.4 Create `HeatmapStreakMapper` for `HeatmapStreak` entity
- [x] 1.5 Create `ReminderNotificationMapper` for `ReminderNotification` entity

## 2. Service Layer Refactoring

- [x] 2.1 Refactor `LearningRoadmapService` to use `LearningRoadmapMapper`, throw `ResourceNotFoundException`, and add `@Transactional`
- [x] 2.2 Refactor `SkillNodeService` to use `SkillNodeMapper`, throw `ResourceNotFoundException`, and add `@Transactional`
- [x] 2.3 Refactor `NodePrerequisiteService` to use `NodePrerequisiteMapper`, throw `ResourceNotFoundException`, and add `@Transactional`
- [x] 2.4 Refactor `HeatmapStreakService` to use `HeatmapStreakMapper`, throw `ResourceNotFoundException`, and add `@Transactional`
- [x] 2.5 Refactor `ReminderNotificationService` to use `ReminderNotificationMapper`, throw `ResourceNotFoundException`, and add `@Transactional`

## 3. Verification

- [x] 3.1 Verify application compiles successfully (`mvn clean compile`)
- [x] 3.2 Ensure no entities are leaked from controllers directly
