# USE CASE: 5 Add a New Employee

## CHARACTERISTIC INFORMATION

### Goal in Context

As an *HR advisor* I want *to add a new employee's details* so that *I can ensure the new employee is paid.*

### Scope

Company.

### Level

Primary task.

### Preconditions

The new employee has been hired and their details are available to HR.

### Success End Condition

The new employee's record exists in the database and the employee can be paid.

### Failed End Condition

No employee record is created.

### Primary Actor

HR Advisor.

### Trigger

A new employee starts work at the company.

## MAIN SUCCESS SCENARIO

1. HR advisor captures the new employee's details.
2. HR advisor adds the employee's details to the system.
3. System stores the new employee record.
4. HR advisor confirms the record has been created.

## EXTENSIONS

2. **Employee details are incomplete or invalid**:
    1. HR advisor corrects the details and resubmits.
3. **Employee already exists**:
    1. System informs the HR advisor that a record already exists.

## SUB-VARIATIONS

None.

## SCHEDULE

**DUE DATE**: Release 1.0