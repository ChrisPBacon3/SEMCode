package com.napier.sem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * The HR system application. Reports on employee data.
 */
public class Main
{
    /** Date that marks a current record. */
    private static final String CURRENT = "'9999-01-01'";

    /** Message for a failed employee lookup. */
    private static final String EMPLOYEE_FAILED = "Failed to get employee details";

    /** Columns used by the salary reports. */
    private static final String SALARY_SELECT =
            "SELECT employees.emp_no, employees.first_name, "
                    + "employees.last_name, salaries.salary ";

    /** SQL for a full employee record. */
    private static final String EMPLOYEE_SELECT =
            "SELECT e.emp_no, e.first_name, e.last_name, t.title, s.salary, "
                    + "d.dept_no, d.dept_name, m.emp_no AS mgr_no, "
                    + "m.first_name AS mgr_first, m.last_name AS mgr_last "
                    + "FROM employees e "
                    + "LEFT JOIN titles t ON t.emp_no = e.emp_no AND t.to_date = " + CURRENT + " "
                    + "LEFT JOIN salaries s ON s.emp_no = e.emp_no AND s.to_date = " + CURRENT + " "
                    + "LEFT JOIN dept_emp de ON de.emp_no = e.emp_no AND de.to_date = " + CURRENT + " "
                    + "LEFT JOIN departments d ON d.dept_no = de.dept_no "
                    + "LEFT JOIN dept_manager dm ON dm.dept_no = de.dept_no "
                    + "AND dm.to_date = " + CURRENT + " "
                    + "LEFT JOIN employees m ON m.emp_no = dm.emp_no "
                    + "WHERE 1 = 1 ";

    /** Connection to the MySQL database. */
    private Connection con;

    /**
     * Connect to the MySQL database.
     * @param location The host and port, for example localhost:33060.
     * @param delay Milliseconds to wait before each connection attempt.
     */
    public void connect(String location, int delay)
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
                Thread.sleep(delay);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://" + location
                                + "/employees?allowPublicKeyRetrieval=true&useSSL=false",
                        "root", "example");
                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + i);
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
            catch (SQLException e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }

    /**
     * Get a single employee record by employee number.
     * @param id The emp_no of the employee.
     * @return The employee, or null if not found or on error.
     */
    public Employee getEmployee(int id)
    {
        try (PreparedStatement pstmt =
                     con.prepareStatement(EMPLOYEE_SELECT + "AND e.emp_no = ?"))
        {
            pstmt.setInt(1, id);
            try (ResultSet rset = pstmt.executeQuery())
            {
                return readEmployee(rset);
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            System.out.println(EMPLOYEE_FAILED);
            return null;
        }
    }

    /**
     * Get a single employee record by first and last name.
     * @param firstName The employee's first name.
     * @param lastName The employee's last name.
     * @return The first matching employee, or null if not found or on error.
     */
    public Employee getEmployee(String firstName, String lastName)
    {
        try (PreparedStatement pstmt = con.prepareStatement(EMPLOYEE_SELECT
                + "AND e.first_name = ? AND e.last_name = ? ORDER BY e.emp_no"))
        {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            try (ResultSet rset = pstmt.executeQuery())
            {
                return readEmployee(rset);
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            System.out.println(EMPLOYEE_FAILED);
            return null;
        }
    }

    /**
     * Builds an Employee from the first row of a result set.
     * Department and manager are filled in where they exist.
     * @param rset The result set from EMPLOYEE_SELECT.
     * @return The employee, or null if the result set is empty.
     * @throws SQLException if a column cannot be read.
     */
    private Employee readEmployee(ResultSet rset) throws SQLException
    {
        if (!rset.next())
        {
            return null;
        }

        Employee mgr = null;
        int mgrNo = rset.getInt("mgr_no");
        if (!rset.wasNull())
        {
            mgr = new Employee();
            mgr.emp_no = mgrNo;
            mgr.first_name = rset.getString("mgr_first");
            mgr.last_name = rset.getString("mgr_last");
        }

        Department dept = null;
        String deptNo = rset.getString("dept_no");
        if (deptNo != null)
        {
            dept = new Department();
            dept.dept_no = deptNo;
            dept.dept_name = rset.getString("dept_name");
            dept.manager = mgr;
        }

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
     * Builds an employee for a salary report from the current row.
     * @param rset The result set, positioned on a row.
     * @return The employee.
     * @throws SQLException if a column cannot be read.
     */
    private Employee readSalary(ResultSet rset) throws SQLException
    {
        Employee emp = new Employee();
        emp.emp_no = rset.getInt("emp_no");
        emp.first_name = rset.getString("first_name");
        emp.last_name = rset.getString("last_name");
        emp.salary = rset.getInt("salary");
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
                            + (emp.manager == null ? null
                            : emp.manager.first_name + " " + emp.manager.last_name)
                            + "\n");
        }
    }

    /**
     * Get a department by name, including its current manager.
     * @param deptName The name of the department.
     * @return The department, or null if not found or on error.
     */
    public Department getDepartment(String deptName)
    {
        String strSelect =
                "SELECT d.dept_no, d.dept_name, m.emp_no, m.first_name, m.last_name "
                        + "FROM departments d "
                        + "JOIN dept_manager dm ON dm.dept_no = d.dept_no "
                        + "JOIN employees m ON m.emp_no = dm.emp_no "
                        + "WHERE d.dept_name = ? AND dm.to_date = " + CURRENT;

        try (PreparedStatement pstmt = con.prepareStatement(strSelect))
        {
            pstmt.setString(1, deptName);
            try (ResultSet rset = pstmt.executeQuery())
            {
                if (!rset.next())
                {
                    return null;
                }
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
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get department details");
            return null;
        }
    }

    /**
     * Gets the current salaries of all employees in a department.
     * @param dept The department to get salaries for.
     * @return The employees and salaries, or an empty list on error.
     */
    public List<Employee> getSalariesByDepartment(Department dept)
    {
        List<Employee> employees = new ArrayList<>();
        if (dept == null)
        {
            return employees;
        }

        String strSelect = SALARY_SELECT
                + "FROM employees, salaries, dept_emp, departments "
                + "WHERE employees.emp_no = salaries.emp_no "
                + "AND employees.emp_no = dept_emp.emp_no "
                + "AND dept_emp.dept_no = departments.dept_no "
                + "AND salaries.to_date = " + CURRENT + " "
                + "AND dept_emp.to_date = " + CURRENT + " "
                + "AND departments.dept_no = ? "
                + "ORDER BY employees.emp_no ASC";

        try (PreparedStatement pstmt = con.prepareStatement(strSelect))
        {
            pstmt.setString(1, dept.dept_no);
            try (ResultSet rset = pstmt.executeQuery())
            {
                while (rset.next())
                {
                    employees.add(readSalary(rset));
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salaries by department");
        }
        return employees;
    }

    /**
     * Gets the current salaries of all employees with a given role.
     * @param title The role (title) to get salaries for.
     * @return The employees and salaries, or an empty list on error.
     */
    public List<Employee> getSalariesByRole(String title)
    {
        List<Employee> employees = new ArrayList<>();

        String strSelect = SALARY_SELECT
                + "FROM employees, salaries, titles "
                + "WHERE employees.emp_no = salaries.emp_no "
                + "AND employees.emp_no = titles.emp_no "
                + "AND salaries.to_date = " + CURRENT + " "
                + "AND titles.to_date = " + CURRENT + " "
                + "AND titles.title = ? "
                + "ORDER BY employees.emp_no ASC";

        try (PreparedStatement pstmt = con.prepareStatement(strSelect))
        {
            pstmt.setString(1, title);
            try (ResultSet rset = pstmt.executeQuery())
            {
                while (rset.next())
                {
                    employees.add(readSalary(rset));
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salaries by role");
        }
        return employees;
    }

    /**
     * Gets all the current employees and salaries.
     * @return All employees and salaries, or an empty list on error.
     */
    public List<Employee> getAllSalaries()
    {
        List<Employee> employees = new ArrayList<>();

        String strSelect = SALARY_SELECT
                + "FROM employees, salaries "
                + "WHERE employees.emp_no = salaries.emp_no "
                + "AND salaries.to_date = " + CURRENT + " "
                + "ORDER BY employees.emp_no ASC";

        try (Statement stmt = con.createStatement();
             ResultSet rset = stmt.executeQuery(strSelect))
        {
            while (rset.next())
            {
                employees.add(readSalary(rset));
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
        }
        return employees;
    }

    /**
     * Adds a new employee to the database.
     * @param emp The employee to add. Needs emp_no and both names.
     */
    public void addEmployee(Employee emp)
    {
        if (emp == null)
        {
            return;
        }
        String strInsert =
                "INSERT INTO employees "
                        + "(emp_no, first_name, last_name, birth_date, gender, hire_date) "
                        + "VALUES (?, ?, ?, " + CURRENT + ", 'M', " + CURRENT + ")";

        try (PreparedStatement pstmt = con.prepareStatement(strInsert))
        {
            pstmt.setInt(1, emp.emp_no);
            pstmt.setString(2, emp.first_name);
            pstmt.setString(3, emp.last_name);
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to add employee");
        }
    }

    /**
     * Prints a list of employees and their salaries.
     * @param employees The list of employees to print.
     */
    public void printSalaries(List<Employee> employees)
    {
        // Check employees is not null
        if (employees == null)
        {
            System.out.println("No employees");
            return;
        }
        // Print header
        System.out.println(String.format("%-10s %-15s %-20s %-8s",
                "Emp No", "First Name", "Last Name", "Salary"));
        // Loop over all employees in the list
        for (Employee emp : employees)
        {
            if (emp == null)
            {
                continue;
            }
            String empString =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(empString);
        }
    }

    /**
     * Runs the application.
     * @param args Optional database location and connection delay.
     */
    public static void main(String[] args)
    {
        // Create new Application and connect to database
        Main app = new Main();

        if (args.length == 0)
        {
            app.connect("localhost:33060", 30_000);
        }
        else
        {
            app.connect(args[0], Integer.parseInt(args[1]));
        }

        Department dept = app.getDepartment("Development");
        List<Employee> employees = app.getSalariesByDepartment(dept);

        // Print salary report
        app.printSalaries(employees);

        // Disconnect from database
        app.disconnect();
    }
}