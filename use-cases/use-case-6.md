# USE CASE: 6 View an Employee Record

## CHARACTERISTIC INFORMATION

### Goal in Context

As an *HR advisor* I want *to view an employee's details* so that *the employee's promotion request can be supported.*

### Scope

Company.

### Level

Primary task.

### Preconditions

We know the employee's ID.  Database contains the employee's current details.

### Success End Condition

The employee's current details (name, title, salary, department and manager) are displayed to the HR advisor.

### Failed End Condition

No employee details are displayed.

### Primary Actor

HR Advisor.

### Trigger

A promotion request for an employee is received by HR.

## MAIN SUCCESS SCENARIO

1. HR advisor receives a promotion request for an employee.
2. HR advisor captures the employee's ID.
3. HR advisor retrieves the employee's current details.
4. System displays the employee's details to the HR advisor.

## EXTENSIONS

3. **Employee does not exist**:
    1. HR advisor informs the requester that no employee record exists.

## SUB-VARIATIONS

None.

## SCHEDULE

**DUE DATE**: Release 1.0