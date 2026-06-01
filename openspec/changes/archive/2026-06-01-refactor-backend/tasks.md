## 1. Setup & Configuration

- [x] 1.1 Add MapStruct dependency and configuration to `pom.xml`
- [x] 1.2 Create custom exceptions (`ResourceNotFoundException`, `BadRequestException`)
- [x] 1.3 Create standard error response DTOs for the API

## 2. Global Exception Handling

- [x] 2.1 Enhance `GlobalExceptionHandler` to handle `MethodArgumentNotValidException`
- [x] 2.2 Enhance `GlobalExceptionHandler` to handle `ResourceNotFoundException`
- [x] 2.3 Enhance `GlobalExceptionHandler` to handle general `Exception` as 500 Internal Server Error

## 3. Enums Implementation

- [x] 3.1 Create `UserRole` enum and replace magic strings in `User` entity and services
- [x] 3.2 Create `UserStatus` enum and replace magic strings in `User` entity and services

## 4. DTO Mappers

- [x] 4.1 Create `UserMapper` using MapStruct to map between `User`, `UserRequest`, and `UserResponse`
- [x] 4.2 Integrate `UserMapper` into `UserService` and `AuthenticationService`, replacing manual mappings

## 5. Service Layer Refactoring

- [x] 5.1 Add `@Transactional` to data-modifying methods in `UserService` and `AuthenticationService`
- [x] 5.2 Refactor `UserService.getUserById` and `updateUser` to throw `ResourceNotFoundException` instead of returning null
- [x] 5.3 Implement `Pageable` in `UserRepository` and refactor `getAllUsers` in `UserService` to return a `Page<UserResponse>`

## 6. Controller Layer Refactoring

- [x] 6.1 Update `UserController` list endpoint to accept `Pageable` parameters and return a paginated response
- [x] 6.2 Update `AuthenticationController` registration to return standard structures mapped by the service (ensure it doesn't leak Entity)
