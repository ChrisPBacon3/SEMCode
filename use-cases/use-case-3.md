# USE CASE: 3 Produce a Report on the Salary of Employees in My Department

## CHARACTERISTIC INFORMATION

### Goal in Context

As a *department manager* I want *to produce a report on the salary of employees in my department* so that *I can support financial reporting for my department.*

### Scope

Company.

### Level

Primary task.

### Preconditions

The department manager is known to the system and manages a department.  Database contains current employee salary data.

### Success End Condition

A report on the department's salaries is available for the department manager.

### Failed End Condition

No report is produced.

### Primary Actor

Department Manager.

### Trigger

The department manager needs salary information for financial reporting or planning.

## MAIN SUCCESS SCENARIO

1. Department manager requests the salary report for their department.
2. System identifies the department managed by the department manager.
3. System extracts current salary information of all employees in that department.
4. System provides the report to the department manager.

## EXTENSIONS

2. **Manager does not manage a department**:
    1. System informs the manager that no department is available.

## SUB-VARIATIONS

None.

## SCHEDULE

**DUE DATE**: Release 1.0