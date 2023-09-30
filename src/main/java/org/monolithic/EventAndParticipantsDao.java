package org.monolithic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Hashtable;

import static org.monolithic.Main.conn;

/**
 * View all Events and Participants functionality
 *
 * @author James Redding
 */

public class EventAndParticipantsDao {

    public static Hashtable<UUID, EventAndParticipants> getAllEventsAndParticipants() {
        Hashtable<UUID, EventAndParticipants> eventsAndParticipantsTable = new Hashtable<UUID, EventAndParticipants>();
        final String GET_ALL_EVENTS_SQL = "SELECT * FROM events";
        try (Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery(GET_ALL_EVENTS_SQL);

            while (rs.next()) {
                UUID key = UUID.fromString(rs.getString("id"));
                EventAndParticipants value = EventAndParticipants.builder()
                        .event(toEvent(rs))
                        .participantList(new ArrayList<>())
                        .build();
                eventsAndParticipantsTable.put(key, value);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve events", e);
        }

        final String GET_ALL_EVENTS_AND_PARTICIPANTS_SQL = """
                        SELECT 
                            participant_in_event.event_id,
                            participant_in_event.participant_id, 
                            participants.name, 
                            participants.email, 
                        FROM (participants inner join participant_in_event 
                            on participants.id = participant_in_event.participant_id) 
                            inner join events on events.id = participant_in_event.event_id
                        """;

        try (Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery(GET_ALL_EVENTS_AND_PARTICIPANTS_SQL);

            while (rs.next()) {
                UUID key = UUID.fromString(rs.getString("event_id"));
                EventAndParticipants newElement = eventsAndParticipantsTable.get(key);
                newElement.addParticipant(toParticipant(rs));
                eventsAndParticipantsTable.replace(key, newElement);
            }

            return eventsAndParticipantsTable;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve events and participants", e);
        }
    }

    private static Participant toParticipant(ResultSet rs) throws SQLException {
        if (rs == null) {
            return null;
        }

        return Participant.builder()
                .participantId(UUID.fromString(rs.getString("participant_id")))
                .participantName(rs.getString("name"))
                .participantEmail(rs.getString("email"))
                .build();
    }

    private static Event toEvent(ResultSet rs) throws SQLException {
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