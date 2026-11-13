package com.napier.sem;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest
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
        assertDoesNotThrow(() -> app.printSalaries(null));
    }

    @Test
    void printSalariesTestEmpty()
    {
        ArrayList<Employee> employees = new ArrayList<Employee>();
        assertDoesNotThrow(() -> app.printSalaries(employees));
    }

    @Test
    void printSalariesTestContainsNull()
    {
        ArrayList<Employee> employees = new ArrayList<Employee>();
        employees.add(null);
        assertDoesNotThrow(() -> app.printSalaries(employees));
    }

    @Test
    void printSalaries()
    {
        ArrayList<Employee> employees = new ArrayList<Employee>();
        Employee emp = new Employee();
        emp.emp_no = 1;
        emp.first_name = "Kevin";
        emp.last_name = "Chalmers";
        emp.title = "Engineer";
        emp.salary = 55000;
        employees.add(emp);
        assertDoesNotThrow(() -> app.printSalaries(employees));
    }

    // ---------- displayEmployee ----------

    @Test
    void displayEmployeeTestNull()
    {
        assertDoesNotThrow(() -> app.displayEmployee(null));
    }

    @Test
    void displayEmployeeTestEmpty()
    {
        Employee emp = new Employee();
        assertDoesNotThrow(() -> app.displayEmployee(emp));
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
        emp.salary = 55000;
        emp.dept = dept;
        emp.manager = mgr;
        assertDoesNotThrow(() -> app.displayEmployee(emp));
    }
}