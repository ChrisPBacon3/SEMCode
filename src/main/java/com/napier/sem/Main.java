package com.napier.sem;

import java.sql.*;

public class Main
{
    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

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
     * Get a single employee record.
     */
    public Employee getEmployee(int ID)
    {
        try
        {
            String strSelect =
                    "SELECT e.emp_no, e.first_name, e.last_name, t.title, s.salary, "
                            + "       d.dept_name, CONCAT(m.first_name, ' ', m.last_name) AS manager "
                            + "FROM employees e "
                            + "JOIN titles t        ON t.emp_no = e.emp_no "
                            + "JOIN salaries s      ON s.emp_no = e.emp_no "
                            + "JOIN dept_emp de     ON de.emp_no = e.emp_no "
                            + "JOIN departments d   ON d.dept_no = de.dept_no "
                            + "JOIN dept_manager dm ON dm.dept_no = de.dept_no "
                            + "JOIN employees m     ON m.emp_no = dm.emp_no "
                            + "WHERE e.emp_no = ? "
                            + "  AND t.to_date  = '9999-01-01' "
                            + "  AND s.to_date  = '9999-01-01' "
                            + "  AND de.to_date = '9999-01-01' "
                            + "  AND dm.to_date = '9999-01-01'";

            PreparedStatement pstmt = con.prepareStatement(strSelect);
            pstmt.setInt(1, ID);
            ResultSet rset = pstmt.executeQuery();

            if (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no     = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name  = rset.getString("last_name");
                emp.title      = rset.getString("title");
                emp.salary     = rset.getInt("salary");
                emp.dept_name  = rset.getString("dept_name");
                emp.manager    = rset.getString("manager");
                return emp;
            }
            return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    /**
     * Display an employee's details.
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
                            + emp.dept_name + "\n"
                            + "Manager: " + emp.manager + "\n");
        }
    }

    public static void main(String[] args)
    {
        // Create new Application
        Main a = new Main();

        // Connect to database
        a.connect();
        // Get Employee
        Employee emp = a.getEmployee(255530);
        // Display results
        a.displayEmployee(emp);

        // Disconnect from database
        a.disconnect();
    }
}