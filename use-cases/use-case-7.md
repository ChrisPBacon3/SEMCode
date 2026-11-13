# USE CASE: 7 Update an Employee Record

## CHARACTERISTIC INFORMATION

### Goal in Context

As an *HR advisor* I want *to update an employee's details* so that *the employee's details are kept up-to-date.*

### Scope

Company.

### Level

Primary task.

### Preconditions

We know the employee's ID and the details that need to change.  The employee's record exists in the database.

### Success End Condition

The employee's record contains the updated details.

### Failed End Condition

The employee's record is unchanged.

### Primary Actor

HR Advisor.

### Trigger

HR is informed that an employee's details have changed (for example a new job title, salary or department).

## MAIN SUCCESS SCENARIO

1. HR advisor is informed of a change to an employee's details.
2. HR advisor captures the employee's ID.
3. HR advisor retrieves the employee's current details.
4. HR advisor enters the updated details.
5. System stores the updated record.
6. HR advisor confirms the record has been updated.

## EXTENSIONS

3. **Employee does not exist**:
    1. HR advisor informs the requester that no employee record exists.
4. **Updated details are invalid**:
    1. System rejects the update and the HR advisor corrects the details and resubmits.

## SUB-VARIATIONS

None.

## SCHEDULE

**DUE DATE**: Release 1.0