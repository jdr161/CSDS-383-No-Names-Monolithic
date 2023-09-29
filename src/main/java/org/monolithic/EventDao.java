package org.monolithic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.monolithic.Main.conn;

public class EventDao {

    public List<Event> getAllEvents() {
        List<Event> eventsList = new ArrayList<>();
        final String GET_ALL_EVENTS_SQL = "SELECT * FROM events";

        try (Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery(GET_ALL_EVENTS_SQL);

            while (rs.next()) {
                eventsList.add(toEvent(rs));
                //Another loop here to go through participants for each event
                    //Pull participants for each event: final String GET_ALL_EVENTS_SQL = "SELECT * FROM events";
                    //
            }

            return eventsList;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve events", e);
        }
    }

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

    public void addEvent(Event event) throws SQLException {
        final String INSERT_EVENT_SQL = """
                INSERT INTO events (id, date, time, title, description, host_email) 
                VALUES (?, ?, ?, ?, ?, ?);""";

        try (PreparedStatement preparedStatement = conn.prepareStatement(INSERT_EVENT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, event.getId().toString());
            preparedStatement.setString(2, event.getDate());
            preparedStatement.setString(3, event.getTime());
            preparedStatement.setString(4, event.getTitle());
            preparedStatement.setString(5, event.getDescription());
            preparedStatement.setString(6, event.getHostEmail());
            preparedStatement.executeUpdate();

            ResultSet res = preparedStatement.getGeneratedKeys();
            res.next(); // TODO throw if no next.
        }
    }

    public boolean doesEventExist(String eventId) {
        final String CHECK_EVENT_EXISTS_SQL = """
                SELECT COUNT(1)
                FROM events
                WHERE id = ?;""";

        try (PreparedStatement preparedStatement = conn.prepareStatement(CHECK_EVENT_EXISTS_SQL)) {
            preparedStatement.setString(1, eventId);
            preparedStatement.executeQuery();
            ResultSet res = preparedStatement.getResultSet();
            res.next();
            boolean participantExists = res.getInt(1) == 1;
            return participantExists;
        }
        catch (SQLException e) {
            return false;
        }
    }

    public boolean doesParticipantExist(String participantId) {
        final String CHECK_PARTICIPANT_EXISTS_SQL = """
                SELECT COUNT(1)
                FROM participants
                WHERE id = ?;""";

        try (PreparedStatement preparedStatement = conn.prepareStatement(CHECK_PARTICIPANT_EXISTS_SQL)) {
            preparedStatement.setString(1, participantId);
            preparedStatement.executeQuery();
            ResultSet res = preparedStatement.getResultSet();
            res.next();
            boolean participantExists = res.getInt(1) == 1;
            return participantExists;
        } catch (SQLException e) {
            return false;
        }
    }

    public void addParticipantInEvent(String participantId, String eventId) throws SQLException {
        final String INSERT_PARTICIPANT_IN_EVENT_SQL = """
                INSERT INTO participant_in_event (participant_id, event_id) 
                VALUES (?, ?);""";

        try (PreparedStatement preparedStatement = conn.prepareStatement(INSERT_PARTICIPANT_IN_EVENT_SQL)) {
            preparedStatement.setString(1, participantId);
            preparedStatement.setString(2, eventId);
            preparedStatement.executeUpdate();
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
