## ADDED Requirements

### Requirement: Service Layer Atomicity
The system SHALL ensure that all data-modifying service operations across all entities are atomic, using transactional boundaries to prevent partial data corruption upon failure.

#### Scenario: Multi-step data modification fails
- **WHEN** a service operation updates multiple records and encounters an error mid-execution
- **THEN** all changes made within that operation are rolled back completely

### Requirement: Centralized DTO Mapping
The system SHALL utilize MapStruct to map between database Entities and API DTOs across all domain areas, strictly preventing Entity leakage to the controller layer.

#### Scenario: Retrieving an entity
- **WHEN** a controller requests data from a service
- **THEN** the service maps the internal Entity to a standardized DTO before returning it, omitting any internal or sensitive fields not explicitly defined in the DTO
