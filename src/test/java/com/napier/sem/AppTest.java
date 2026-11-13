package com.napier.sem;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unit tests that do not need a database connection.
 */
class AppTest
{
    static Main app;

    @BeforeAll
    static void init()
    {
        app = new Main();
    }

    // ---------- printSalaries ----------

    @Test
    void printSalariesTestNull()
    {
        assertDoesNotThrow(() -> app.printSalaries(null), "Printing null should not throw");
    }

    @Test
    void printSalariesTestEmpty()
    {
        List<Employee> employees = new ArrayList<>();
        assertDoesNotThrow(() -> app.printSalaries(employees), "Printing an empty list should not throw");
    }

    @Test
    void printSalariesTestContainsNull()
    {
        List<Employee> employees = new ArrayList<>();
        employees.add(null);
        assertDoesNotThrow(() -> app.printSalaries(employees), "Printing a list containing null should not throw");
    }

    @Test
    void printSalaries()
    {
        List<Employee> employees = new ArrayList<>();
        Employee emp = new Employee();
        emp.emp_no = 1;
        emp.first_name = "Kevin";
        emp.last_name = "Chalmers";
        emp.title = "Engineer";
        emp.salary = 55_000;
        employees.add(emp);
        assertDoesNotThrow(() -> app.printSalaries(employees), "Printing a normal list should not throw");
    }

    // ---------- displayEmployee ----------

    @Test
    void displayEmployeeTestNull()
    {
        assertDoesNotThrow(() -> app.displayEmployee(null), "Displaying null should not throw");
    }

    @Test
    void displayEmployeeTestEmpty()
    {
        Employee emp = new Employee();
        assertDoesNotThrow(() -> app.displayEmployee(emp), "Displaying an empty employee should not throw");
    }

    @Test
    void displayEmployee()
    {
        Employee mgr = new Employee();
        mgr.emp_no = 2;
        mgr.first_name = "Leon";
        mgr.last_name = "DasSarma";

        Department dept = new Department();
        dept.dept_no = "d005";
        dept.dept_name = "Development";
        dept.manager = mgr;

        Employee emp = new Employee();
        emp.emp_no = 1;
        emp.first_name = "Kevin";
        emp.last_name = "Chalmers";
        emp.title = "Engineer";
        emp.salary = 55_000;
        emp.dept = dept;
        emp.manager = mgr;
        assertDoesNotThrow(() -> app.displayEmployee(emp), "Displaying a full employee should not throw");
    }
}