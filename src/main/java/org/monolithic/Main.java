package org.monolithic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

public class Main {

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

    private static void setupDatabase() throws SQLException {
        // create the events table:
        try (Statement statement = conn.createStatement()) {
            statement.execute(CREATE_EVENTS_TABLE_SQL);
        }

        // create the participants table:
        try (Statement statement = conn.createStatement()) {
            statement.execute(CREATE_PARTICIPANTS_TABLE_SQL);
        }
    }

    // TODO REMOVE THIS - THIS IS ALL JUST EXAMPLES/CONFIRMATION THAT IT WORKS:
    private static void exampleDatabaseOperations() throws SQLException {
        // example of adding an event row:
        String exampleEventId;
        Event exampleEvent = Event.builder()
                .date("2023-09-23")
                .time("11:00 PM")
                .title("example title")
                .description("example description")
                .hostEmail("example")
                .build();
        try (PreparedStatement preparedStatement = conn.prepareStatement(INSERT_EVENT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, exampleEvent.getDate());
            preparedStatement.setString(2, exampleEvent.getTime());
            preparedStatement.setString(3, exampleEvent.getTitle());
            preparedStatement.setString(4, exampleEvent.getDescription());
            preparedStatement.setString(5, exampleEvent.getHostEmail());
            preparedStatement.executeUpdate();

            ResultSet res = preparedStatement.getGeneratedKeys();
            res.next(); // TODO throw if no next.
            exampleEventId = res.getString(1);
        }

        // example querying id of the event we added:
        try (Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery("select * from events");
            rs.next();
            String id = rs.getString("id");
            String title = rs.getString("title");
            System.out.printf("""
                            --- Successfully added event ---
                            Id: %s
                            Date: %s
                            Time: %s
                            Title: %s
                            Desc: %s
                            Host email: %s
                            """,
                    rs.getString("id"),
                    rs.getString("date"),
                    rs.getString("time"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("host_email"));
        }

        // example of adding a participant row, which is linked to the event we previously added:
        try (PreparedStatement preparedStatement = conn.prepareStatement(INSERT_PARTICIPANT_SQL)) {
            preparedStatement.setString(1, exampleEventId);
            preparedStatement.setString(2, "time here");
            preparedStatement.setString(3, "name here");
            preparedStatement.setString(4, "email here");
            preparedStatement.executeUpdate();
        }

        // This is Felix --
        // I'm putting this here if you wanna see inserting/getting events with the Event stuff I added but otherwise I will delete later
        try {
            Event haha = Event.builder()
                    .date("2023-09-23")
                    .time("11:00 PM")
                    .title("ex title")
                    .description("ex desc")
                    .hostEmail("ex@gmail.com")
                    .build();
            Event haha2 = Event.builder()
                    .date("2023-09-24")
                    .time("10:00 PM")
                    .title("ex title 2")
                    .description("ex desc 2")
                    .hostEmail("ex2@gmail.com")
                    .build();
            EventDao eventDao = new EventDaoImpl();
            eventDao.addEvent(haha);
            eventDao.addEvent(haha2);

            List<Event> events = eventDao.getAllEvents();
            for (Event e : events) {
                System.out.println(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void printMenu(String[] options) {
        for (String option : options) {
            System.out.println(option);
        }
        System.out.print("Choose your option : ");
    }
}