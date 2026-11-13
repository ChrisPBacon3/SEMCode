package com.napier.sem;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests that run against the employees database.
 */
class AppIntegrationTest
{
    static Main app;

    @BeforeAll
    static void init()
    {
        app = new Main();
        app.connect("localhost:33060", 30_000);
    }

    // ---------- getEmployee ----------

    @Test
    void testGetEmployee()
    {
        Employee emp = app.getEmployee(255_530);
        assertNotNull(emp, "Employee 255530 should exist");
        assertEquals(255_530, emp.emp_no, "Employee number should match");
        assertEquals("Ronghao", emp.first_name, "First name should match");
        assertEquals("Garigliano", emp.last_name, "Last name should match");
        assertEquals("Technique Leader", emp.title, "Current title should match");
        assertEquals(57_499, emp.salary, "Current salary should match");
        assertEquals("Development", emp.dept.dept_name, "Department name should match");
        assertEquals("Leon", emp.manager.first_name, "Manager first name should match");
        assertEquals("DasSarma", emp.manager.last_name, "Manager last name should match");
    }

    @Test
    void testGetEmployeeNotFound()
    {
        assertNull(app.getEmployee(1), "Employee 1 should not exist");
    }

    @Test
    void testGetEmployeeByName()
    {
        Employee emp = app.getEmployee("Ronghao", "Garigliano");
        assertNotNull(emp, "Employee should be found by name");
        assertEquals("Ronghao", emp.first_name, "First name should match");
        assertEquals("Garigliano", emp.last_name, "Last name should match");
    }

    @Test
    void testGetEmployeeByNameNotFound()
    {
        assertNull(app.getEmployee("Nobody", "Nowhere"), "Unknown name should return null");
    }

    // ---------- getDepartment ----------

    @Test
    void testGetDepartment()
    {
        Department dept = app.getDepartment("Development");
        assertNotNull(dept, "Development department should exist");
        assertEquals("d005", dept.dept_no, "Department number should match");
        assertEquals("Development", dept.dept_name, "Department name should match");
        assertEquals("Leon", dept.manager.first_name, "Manager first name should match");
        assertEquals("DasSarma", dept.manager.last_name, "Manager last name should match");
    }

    @Test
    void testGetDepartmentNotFound()
    {
        assertNull(app.getDepartment("No Such Department"), "Unknown department should return null");
    }

    // ---------- getSalariesByDepartment ----------

    @Test
    void testGetSalariesByDepartment()
    {
        Department dept = app.getDepartment("Sales");
        List<Employee> employees = app.getSalariesByDepartment(dept);
        assertFalse(employees.isEmpty(), "Sales should have employees");
        for (Employee emp : employees)
        {
            assertTrue(emp.salary > 0, "Every salary should be positive");
        }
    }

    @Test
    void testGetSalariesByDepartmentNull()
    {
        List<Employee> employees = app.getSalariesByDepartment(null);
        assertNotNull(employees, "Null department should give an empty list");
        assertTrue(employees.isEmpty(), "Null department should have no employees");
    }

    // ---------- getSalariesByRole ----------

    @Test
    void testGetSalariesByRole()
    {
        List<Employee> employees = app.getSalariesByRole("Engineer");
        assertFalse(employees.isEmpty(), "There should be engineers");
        Employee last = employees.get(employees.size() - 1);
        assertEquals(499_999, last.emp_no, "Last engineer should be employee 499999");
        assertEquals(77_303, last.salary, "Last engineer's salary should match");
    }

    @Test
    void testGetSalariesByRoleNotFound()
    {
        List<Employee> employees = app.getSalariesByRole("No Such Role");
        assertNotNull(employees, "Unknown role should return an empty list, not null");
        assertTrue(employees.isEmpty(), "Unknown role should have no employees");
    }

    // ---------- getAllSalaries ----------

    @Test
    void testGetAllSalaries()
    {
        List<Employee> employees = app.getAllSalaries();
        assertEquals(240_124, employees.size(), "There should be 240124 current salaries");
    }

    // ---------- addEmployee ----------

    @Test
    void testAddEmployee()
    {
        Employee emp = new Employee();
        emp.emp_no = 500_000;
        emp.first_name = "Kevin";
        emp.last_name = "Chalmers";
        app.addEmployee(emp);
        emp = app.getEmployee(500_000);
        assertNotNull(emp, "Added employee should be found");
        assertEquals(500_000, emp.emp_no, "Employee number should match");
        assertEquals("Kevin", emp.first_name, "First name should match");
        assertEquals("Chalmers", emp.last_name, "Last name should match");
    }

    @Test
    void testAddEmployeeNull()
    {
        assertDoesNotThrow(() -> app.addEmployee(null), "Adding a null employee should not throw");
    }
}