package org.monolithic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Main {

    // TODO refactor
    private static final String CREATE_EVENTS_TABLE_SQL = """
            create table events (
                id uuid default random_uuid() primary key, date varchar(20), time varchar(20), title varchar(50), description varchar(50), host_email varchar(20)
            );""";

    private static final String CREATE_PARTICIPANTS_TABLE_SQL = """
            create table participants (
                id uuid default random_uuid() primary key, name varchar(20), email varchar(20)
            );""";

    private static final String CREATE_PARTICIPANT_IN_EVENT_TABLE_SQL = """
            create table participant_in_event (
                participant_id uuid, event_id uuid,
                primary key (participant_id, event_id),
                foreign key (participant_id) references participants(id), foreign key (event_id) references events(id)
            );""";
    private static final String INSERT_PARTICIPANT_SQL = """
            INSERT INTO participants (event_id, name, email)
            VALUES (?, ?, ?);""";
    // TODO refactor
    static Connection conn;

    public static void main(String[] args) throws SQLException {
        //System.out.println("Starting...");
        conn = connect();
        setupDatabase();
        //exampleDatabaseOperations();

        CliCode code = CliCode.MAIN_MENU;
        while (code == CliCode.MAIN_MENU) {
            code = MainCli.mainMenu();
        }

        //System.out.println("Exiting program...");
        conn.close();
        System.exit(0);
    }

    /**
     * Connect to a sample database
     */
    public static Connection connect() throws SQLException {
        String url = "jdbc:h2:mem:test";
        Connection conn = DriverManager.getConnection(url); // TODO conn resource leak - maybe can use try w resources?
        //System.out.println("Connection to SQLite has been established.");
        return conn;
    }

    private static void setupDatabase() throws SQLException {
        // create the events table:
        try (Statement statement = conn.createStatement()) {
            statement.execute(CREATE_EVENTS_TABLE_SQL);
        }

        // create the participants table:
        try (Statement statement = conn.createStatement()) {
            statement.execute(CREATE_PARTICIPANTS_TABLE_SQL);
        }

        // create the participant_in_event table:
        try (Statement statement = conn.createStatement()) {
            statement.execute(CREATE_PARTICIPANT_IN_EVENT_TABLE_SQL);
        }
    }
}