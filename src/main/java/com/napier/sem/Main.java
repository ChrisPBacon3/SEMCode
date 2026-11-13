package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;

public class Main
{
    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Base SQL for a full employee record: title, salary, department and department manager.
     */
    private static final String EMPLOYEE_SELECT =
            "SELECT e.emp_no, e.first_name, e.last_name, t.title, s.salary, "
                    + "       d.dept_no, d.dept_name, "
                    + "       m.emp_no AS mgr_no, m.first_name AS mgr_first, m.last_name AS mgr_last "
                    + "FROM employees e "
                    + "JOIN titles t        ON t.emp_no = e.emp_no "
                    + "JOIN salaries s      ON s.emp_no = e.emp_no "
                    + "JOIN dept_emp de     ON de.emp_no = e.emp_no "
                    + "JOIN departments d   ON d.dept_no = de.dept_no "
                    + "JOIN dept_manager dm ON dm.dept_no = de.dept_no "
                    + "JOIN employees m     ON m.emp_no = dm.emp_no "
                    + "WHERE t.to_date  = '9999-01-01' "
                    + "  AND s.to_date  = '9999-01-01' "
                    + "  AND de.to_date = '9999-01-01' "
                    + "  AND dm.to_date = '9999-01-01' ";

    /**
     * Connect to the MySQL database.
     */
    public void connect()
    {
        try
        {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database...");
            try
            {
                // Wait a bit for db to start
                Thread.sleep(30000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://db:3306/employees?allowPublicKeyRetrieval=true&useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                // Close connection
                con.close();
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }

    /**
     * Get a single employee record by employee number.
     * @param ID emp_no of the employee.
     * @return The employee, or null if not found or on error.
     */
    public Employee getEmployee(int ID)
    {
        try
        {
            PreparedStatement pstmt = con.prepareStatement(EMPLOYEE_SELECT + "AND e.emp_no = ?");
            pstmt.setInt(1, ID);
            return readEmployee(pstmt.executeQuery());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    /**
     * Get a single employee record by first and last name.
     * @param first_name The employee's first name.
     * @param last_name The employee's last name.
     * @return The first matching employee, or null if not found or on error.
     */
    public Employee getEmployee(String first_name, String last_name)
    {
        try
        {
            PreparedStatement pstmt = con.prepareStatement(
                    EMPLOYEE_SELECT + "AND e.first_name = ? AND e.last_name = ? ORDER BY e.emp_no");
            pstmt.setString(1, first_name);
            pstmt.setString(2, last_name);
            return readEmployee(pstmt.executeQuery());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    /**
     * Builds an Employee, with department and manager, from the first row of a result set.
     * @param rset The result set from EMPLOYEE_SELECT.
     * @return The employee, or null if the result set is empty.
     */
    private Employee readEmployee(ResultSet rset) throws SQLException
    {
        if (!rset.next())
            return null;

        Employee mgr = new Employee();
        mgr.emp_no = rset.getInt("mgr_no");
        mgr.first_name = rset.getString("mgr_first");
        mgr.last_name = rset.getString("mgr_last");

        Department dept = new Department();
        dept.dept_no = rset.getString("dept_no");
        dept.dept_name = rset.getString("dept_name");
        dept.manager = mgr;

        Employee emp = new Employee();
        emp.emp_no = rset.getInt("emp_no");
        emp.first_name = rset.getString("first_name");
        emp.last_name = rset.getString("last_name");
        emp.title = rset.getString("title");
        emp.salary = rset.getInt("salary");
        emp.dept = dept;
        emp.manager = mgr;
        return emp;
    }

    /**
     * Display an employee's details.
     * @param emp The employee to display.
     */
    public void displayEmployee(Employee emp)
    {
        if (emp != null)
        {
            System.out.println(
                    emp.emp_no + " "
                            + emp.first_name + " "
                            + emp.last_name + "\n"
                            + emp.title + "\n"
                            + "Salary:" + emp.salary + "\n"
                            + (emp.dept == null ? null : emp.dept.dept_name) + "\n"
                            + "Manager: "
                            + (emp.manager == null ? null : emp.manager.first_name + " " + emp.manager.last_name)
                            + "\n");
        }
    }

    /**
     * Get a department by name, including its current manager.
     * @param dept_name The name of the department.
     * @return The department, or null if not found or on error.
     */
    public Department getDepartment(String dept_name)
    {
        try
        {
            String strSelect =
                    "SELECT d.dept_no, d.dept_name, m.emp_no, m.first_name, m.last_name "
                            + "FROM departments d "
                            + "JOIN dept_manager dm ON dm.dept_no = d.dept_no "
                            + "JOIN employees m     ON m.emp_no = dm.emp_no "
                            + "WHERE d.dept_name = ? "
                            + "  AND dm.to_date = '9999-01-01'";

            PreparedStatement pstmt = con.prepareStatement(strSelect);
            pstmt.setString(1, dept_name);
            ResultSet rset = pstmt.executeQuery();

            if (rset.next())
            {
                Employee mgr = new Employee();
                mgr.emp_no = rset.getInt("emp_no");
                mgr.first_name = rset.getString("first_name");
                mgr.last_name = rset.getString("last_name");

                Department dept = new Department();
                dept.dept_no = rset.getString("dept_no");
                dept.dept_name = rset.getString("dept_name");
                dept.manager = mgr;
                return dept;
            }
            return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get department details");
            return null;
        }
    }

    /**
     * Gets the current salaries of all employees currently in a department.
     * @param dept The department to get salaries for.
     * @return A list of employees and salaries, or null if there is an error.
     */
    public ArrayList<Employee> getSalariesByDepartment(Department dept)
    {
        if (dept == null)
            return null;
        try
        {
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries, dept_emp, departments "
                            + "WHERE employees.emp_no = salaries.emp_no "
                            + "AND employees.emp_no = dept_emp.emp_no "
                            + "AND dept_emp.dept_no = departments.dept_no "
                            + "AND salaries.to_date = '9999-01-01' "
                            + "AND dept_emp.to_date = '9999-01-01' "
                            + "AND departments.dept_no = ? "
                            + "ORDER BY employees.emp_no ASC";

            PreparedStatement pstmt = con.prepareStatement(strSelect);
            pstmt.setString(1, dept.dept_no);
            ResultSet rset = pstmt.executeQuery();

            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.salary = rset.getInt("salary");
                employees.add(emp);
            }
            return employees;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salaries by department");
            return null;
        }
    }

    /**
     * Gets the current salaries of all employees with a given role (title).
     * @param title The role to get salaries for.
     * @return A list of employees and salaries, or null if there is an error.
     */
    public ArrayList<Employee> getSalariesByRole(String title)
    {
        try
        {
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries, titles "
                            + "WHERE employees.emp_no = salaries.emp_no "
                            + "AND employees.emp_no = titles.emp_no "
                            + "AND salaries.to_date = '9999-01-01' "
                            + "AND titles.to_date = '9999-01-01' "
                            + "AND titles.title = ? "
                            + "ORDER BY employees.emp_no ASC";

            PreparedStatement pstmt = con.prepareStatement(strSelect);
            pstmt.setString(1, title);
            ResultSet rset = pstmt.executeQuery();

            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.salary = rset.getInt("salary");
                employees.add(emp);
            }
            return employees;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salaries by role");
            return null;
        }
    }

    /**
     * Gets all the current employees and salaries.
     * @return A list of all employees and salaries, or null if there is an error.
     */
    public ArrayList<Employee> getAllSalaries()
    {
        try
        {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries "
                            + "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' "
                            + "ORDER BY employees.emp_no ASC";
            ResultSet rset = stmt.executeQuery(strSelect);
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.salary = rset.getInt("salary");
                employees.add(emp);
            }
            return employees;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    /**
     * Prints a list of employees and their salaries.
     * @param employees The list of employees to print.
     */
    public void printSalaries(ArrayList<Employee> employees)
    {
        // Check employees is not null
        if (employees == null)
        {
            System.out.println("No employees");
            return;
        }
        // Print header
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        // Loop over all employees in the list
        for (Employee emp : employees)
        {
            if (emp == null)
                continue;
            String emp_string =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(emp_string);
        }
    }

    public static void main(String[] args)
    {
        // Create new Application
        Main a = new Main();

        // Connect to database
        a.connect();

        // Get an employee by ID and by name
        a.displayEmployee(a.getEmployee(255530));
        a.displayEmployee(a.getEmployee("Ronghao", "Garigliano"));

        // Get salaries for a department
        Department dept = a.getDepartment("Sales");
        ArrayList<Employee> employees = a.getSalariesByDepartment(dept);

        // Print the salaries
        a.printSalaries(employees);

        // Disconnect from database
        a.disconnect();
    }
}