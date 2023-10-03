package org.monolithic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    // TODO refactor
    private static final String CREATE_EVENTS_TABLE_SQL = """
            create table if not exists events (
                id uuid default random_uuid() primary key, date varchar(20), time varchar(20), title varchar(255), description varchar(600), host_email varchar(254)
            );""";

    private static final String CREATE_PARTICIPANTS_TABLE_SQL = """
            create table if not exists participants (
                id uuid default random_uuid() primary key, name varchar(600), email varchar(254)
            );""";

    private static final String CREATE_PARTICIPANT_IN_EVENT_TABLE_SQL = """
            create table if not exists participant_in_event (
                participant_id uuid, event_id uuid,
                primary key (participant_id, event_id),
                foreign key (participant_id) references participants(id), foreign key (event_id) references events(id)
            );""";

    // TODO refactor
    static Connection conn;

    public static void main(String[] args) throws SQLException {
        conn = connect();
        setupDatabase();

        CliCode code = CliCode.MAIN_MENU;
        while (code == CliCode.MAIN_MENU) {
            code = MainCli.mainMenu();
        }

        conn.close();
        System.exit(0);
    }

    /**
     * Connect to a sample database
     */
    public static Connection connect() throws SQLException {
        String url = "jdbc:h2:file:./csds383-monolithic-db";
        Connection conn = DriverManager.getConnection(url); // TODO conn resource leak - maybe can use try w resources?
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