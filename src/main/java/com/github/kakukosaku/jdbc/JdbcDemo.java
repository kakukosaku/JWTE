package com.github.kakukosaku.jdbc;

import java.sql.*;

/**
 * Description
 *
 *  Demo database:
 *      https://github.com/datacharmer/test_db
 *
 * @author kaku
 * Date    2020/6/22
 */
public class JdbcDemo {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/employees";

    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName(JDBC_DRIVER);
        System.out.println("connecting to database...");
        try (
                Connection conn = DriverManager.getConnection(DB_URL, "username", "password");
                Statement stmt = conn.createStatement();
        ) {

            String sql = "SELECT emp_no, dept_no, from_date, to_date FROM current_dept_emp LIMIT 10";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int empNo = rs.getInt("emp_no");
                String deptNo = rs.getString("dept_no");
                Date fromDate = rs.getDate("from_date");
                Date toDate = rs.getDate("to_date");
                System.out.println(String.valueOf(empNo) + " " + deptNo + " " + fromDate.toString() + " " + toDate.toString());
            }

            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
