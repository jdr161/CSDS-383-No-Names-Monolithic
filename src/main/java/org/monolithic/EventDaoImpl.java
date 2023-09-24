package org.monolithic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.monolithic.Main.conn;

public class EventDaoImpl implements EventDao {

    @Override
    public List<Event> getAllEvents() {
        List<Event> eventsList = new ArrayList<>();
        final String GET_ALL_EVENTS_SQL = "SELECT * FROM events";

        try (Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery(GET_ALL_EVENTS_SQL);

            while (rs.next()) {
                eventsList.add(toEvent(rs));
            }

            return eventsList;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve events", e);
        }
    }

    @Override
    public Event getEventById(UUID id) {
        final String GET_EVENT_SQL = "SELECT * FROM events WHERE id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(GET_EVENT_SQL)) {
            preparedStatement.setString(1, id.toString());
            ResultSet rs = preparedStatement.executeQuery();
            boolean eventExists = rs.next();
            if (eventExists) {
                return toEvent(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void addEvent(Event event) throws SQLException {
        final String INSERT_EVENT_SQL = """
                INSERT INTO events (date, time, title, description, host_email) 
                VALUES (?, ?, ?, ?, ?);""";

        try (PreparedStatement preparedStatement = conn.prepareStatement(INSERT_EVENT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, event.getDate());
            preparedStatement.setString(2, event.getTime());
            preparedStatement.setString(3, event.getTitle());
            preparedStatement.setString(4, event.getDescription());
            preparedStatement.setString(5, event.getHostEmail());
            preparedStatement.executeUpdate();

            ResultSet res = preparedStatement.getGeneratedKeys();
            res.next(); // TODO throw if no next.
        }
    }

    private Event toEvent(ResultSet rs) throws SQLException {
        if (rs == null) {
            return null;
        }

        return Event.builder()
                .id(UUID.fromString(rs.getString("id")))
                .date(rs.getString("date"))
                .time(rs.getString("time"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .hostEmail(rs.getString("host_email"))
                .build();
    }
}
