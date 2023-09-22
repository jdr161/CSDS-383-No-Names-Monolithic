package org.monolithic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        connect();
        System.out.println("Hello world!");
        String[] options = {"\n OPTIONS",
                "[1] Create an Event",
                "[2] Register Participants",
                "[3] ",
                "[4] ",
                "[5] ",
        };
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        while (true) {
            printMenu(options);
        }
    }
    /**
     * Connect to a sample database
     */
    public static void connect() throws SQLException {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:h2:mem:test";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    public static void printMenu(String[] options){
        for (String option : options){
            System.out.println(option);
        }
        System.out.print("Choose your option : ");
    }
}