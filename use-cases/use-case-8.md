# USE CASE: 8 Delete an Employee Record

## CHARACTERISTIC INFORMATION

### Goal in Context

As an *HR advisor* I want *to delete an employee's details* so that *the company is compliant with data retention legislation.*

### Scope

Company.

### Level

Primary task.

### Preconditions

We know the employee's ID.  The employee's record exists in the database.

### Success End Condition

The employee's record has been removed from the database.

### Failed End Condition

The employee's record is unchanged.

### Primary Actor

HR Advisor.

### Trigger

An employee's details are no longer required to be kept under the company's data retention policy.

## MAIN SUCCESS SCENARIO

1. HR advisor identifies an employee whose details must be deleted.
2. HR advisor captures the employee's ID.
3. HR advisor retrieves the employee's current details to confirm the correct record.
4. HR advisor requests deletion of the record.
5. System deletes the employee record.
6. HR advisor confirms the record has been deleted.

## EXTENSIONS

3. **Employee does not exist**:
    1. HR advisor informs the requester that no employee record exists.
4. **HR advisor cancels the deletion**:
    1. The record is left unchanged.

## SUB-VARIATIONS

None.

## SCHEDULE

**DUE DATE**: Release 1.0