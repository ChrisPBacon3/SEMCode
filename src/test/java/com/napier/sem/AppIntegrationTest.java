package com.napier.sem;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AppIntegrationTest
{
    static Main app;

    @BeforeAll
    static void init()
    {
        app = new Main();
        app.connect("localhost:33060", 30000);
    }

    // ---------- getEmployee ----------

    @Test
    void testGetEmployee()
    {
        Employee emp = app.getEmployee(255530);
        assertNotNull(emp);
        assertEquals(255530, emp.emp_no);
        assertEquals("Ronghao", emp.first_name);
        assertEquals("Garigliano", emp.last_name);
        assertEquals("Technique Leader", emp.title);
        assertEquals(57499, emp.salary);
        assertEquals("Development", emp.dept.dept_name);
        assertEquals("Leon", emp.manager.first_name);
        assertEquals("DasSarma", emp.manager.last_name);
    }

    @Test
    void testGetEmployeeNotFound()
    {
        assertNull(app.getEmployee(1));
    }

    @Test
    void testGetEmployeeByName()
    {
        Employee emp = app.getEmployee("Ronghao", "Garigliano");
        assertNotNull(emp);
        assertEquals("Ronghao", emp.first_name);
        assertEquals("Garigliano", emp.last_name);
    }

    @Test
    void testGetEmployeeByNameNotFound()
    {
        assertNull(app.getEmployee("Nobody", "Nowhere"));
    }

    // ---------- getDepartment ----------

    @Test
    void testGetDepartment()
    {
        Department dept = app.getDepartment("Development");
        assertNotNull(dept);
        assertEquals("d005", dept.dept_no);
        assertEquals("Development", dept.dept_name);
        assertEquals("Leon", dept.manager.first_name);
        assertEquals("DasSarma", dept.manager.last_name);
    }

    @Test
    void testGetDepartmentNotFound()
    {
        assertNull(app.getDepartment("No Such Department"));
    }

    // ---------- getSalariesByDepartment ----------

    @Test
    void testGetSalariesByDepartment()
    {
        Department dept = app.getDepartment("Sales");
        ArrayList<Employee> employees = app.getSalariesByDepartment(dept);
        assertNotNull(employees);
        assertFalse(employees.isEmpty());
        for (Employee emp : employees)
            assertTrue(emp.salary > 0);
    }

    @Test
    void testGetSalariesByDepartmentNull()
    {
        assertNull(app.getSalariesByDepartment(null));
    }

    // ---------- getSalariesByRole ----------

    @Test
    void testGetSalariesByRole()
    {
        ArrayList<Employee> employees = app.getSalariesByRole("Engineer");
        assertNotNull(employees);
        assertFalse(employees.isEmpty());
        Employee last = employees.get(employees.size() - 1);
        assertEquals(499999, last.emp_no);
        assertEquals(77303, last.salary);
    }

    @Test
    void testGetSalariesByRoleNotFound()
    {
        ArrayList<Employee> employees = app.getSalariesByRole("No Such Role");
        assertNotNull(employees);
        assertTrue(employees.isEmpty());
    }

    // ---------- getAllSalaries ----------

    @Test
    void testGetAllSalaries()
    {
        ArrayList<Employee> employees = app.getAllSalaries();
        assertNotNull(employees);
        assertEquals(240124, employees.size());
    }

    // ---------- addEmployee ----------

    @Test
    void testAddEmployee()
    {
        Employee emp = new Employee();
        emp.emp_no = 500000;
        emp.first_name = "Kevin";
        emp.last_name = "Chalmers";
        app.addEmployee(emp);
        emp = app.getEmployee(500000);
        assertNotNull(emp);
        assertEquals(500000, emp.emp_no);
        assertEquals("Kevin", emp.first_name);
        assertEquals("Chalmers", emp.last_name);
    }

    @Test
    void testAddEmployeeNull()
    {
        assertDoesNotThrow(() -> app.addEmployee(null));
    }
}