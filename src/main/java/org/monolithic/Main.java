package org.monolithic;

import java.sql.*;
import java.util.Scanner;

public class Main {

    // TODO refactor
    static Connection conn;

    public static void main(String[] args) throws SQLException {
        System.out.println("Starting...");
        conn = connect();
        setupDatabase();
        exampleDatabaseOperations();

        String[] options = {"\n OPTIONS",
                "[1] Create an Event",
                "[2] Register Participants",
                "[3] ",
                "[4] ",
                "[5] ",
        };
        Scanner scanner = new Scanner(System.in);
        int option = 0;
//        while (true) { // bro really busted my computer w this shit 💀
//            printMenu(options);
//        }

        conn.close();
    }
    /**
     * Connect to a sample database
     */
    public static Connection connect() throws SQLException {
        String url = "jdbc:h2:mem:test";
        Connection conn = DriverManager.getConnection(url); // TODO conn resource leak - maybe can use try w resources?
        System.out.println("Connection to SQLite has been established.");
        return conn;
    }


    // TODO refactor
    private static final String CREATE_EVENTS_TABLE_SQL = """
        create table events (
            id uuid default random_uuid() primary key, date varchar(20), time varchar(20), title varchar(20), description varchar(20), host_email varchar(20)
        );""";

    private static final String INSERT_EVENT_SQL = """
        INSERT INTO events (date, time, title, description, host_email) 
        VALUES (?, ?, ?, ?, ?);""";

    private static final String CREATE_PARTICIPANTS_TABLE_SQL = """
        create table participants (
            id uuid default random_uuid() primary key, event_id uuid, time varchar(20), name varchar(20), email varchar(20),
            foreign key (event_id) references events(id)
        );""";

    private static final String INSERT_PARTICIPANT_SQL = """
        INSERT INTO participants (event_id, time, name, email)
        VALUES (?, ?, ?, ?);""";

    private static void setupDatabase() throws SQLException {
        // create the events table:
        try (Statement statement = conn.createStatement()){
            statement.execute(CREATE_EVENTS_TABLE_SQL);
        }

        // create the participants table:
        try (Statement statement = conn.createStatement()){
            statement.execute(CREATE_PARTICIPANTS_TABLE_SQL);
        }
    }

    // TODO REMOVE THIS - THIS IS ALL JUST EXAMPLES/CONFIRMATION THAT IT WORKS:
    private static void exampleDatabaseOperations() throws SQLException {
        // example of adding an event row:
        String exampleEventId;
        try (PreparedStatement preparedStatement = conn.prepareStatement(INSERT_EVENT_SQL, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, "date here");
            preparedStatement.setString(2, "time here");
            preparedStatement.setString(3, "title here");
            preparedStatement.setString(4, "desc here");
            preparedStatement.setString(5, "email here");
            preparedStatement.executeUpdate();

            ResultSet res = preparedStatement.getGeneratedKeys();
            res.next(); // TODO throw if no next.
            exampleEventId = res.getString(1);
        }

        // example querying id of the event we added:
        try (Statement statement = conn.createStatement()){
            ResultSet rs = statement.executeQuery("select * from events");
            rs.next();
            String id = rs.getString("id");
            String title = rs.getString("title");
            System.out.println("successfully added event: " + id + ", " + title);
        }

        // example of adding a participant row, which is linked to the event we previously added:
        try (PreparedStatement preparedStatement = conn.prepareStatement(INSERT_PARTICIPANT_SQL)){
            preparedStatement.setString(1, exampleEventId);
            preparedStatement.setString(2, "time here");
            preparedStatement.setString(3, "name here");
            preparedStatement.setString(4, "email here");
            preparedStatement.executeUpdate();
        }
    }

    public static void printMenu(String[] options){
        for (String option : options){
            System.out.println(option);
        }
        System.out.print("Choose your option : ");
    }
}