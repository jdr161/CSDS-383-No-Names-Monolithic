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
        final String GET_ALL_EVENTS_AND_PARTICIPANTS_SQL = """
                        SELECT 
                            participant_in_event.participant_id, 
                            participant.name, 
                            participant.email, 
                            participant_in_event.event_id,
                            event.date,
                            event.time,
                            event.title,
                            event.description,
                            event.host_email 
                        FROM (participants inner join participant_in_event 
                            on participants.id = participant_in_event.participant_id) 
                            inner join events on events.id = participant_in_event.event_id
                        """;

        try (Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery(GET_ALL_EVENTS_AND_PARTICIPANTS_SQL);

            while (rs.next()) {
                if (rs == null) {
                    return null;
                } else if (eventsAndParticipantsTable.containsKey(UUID.fromString(rs.getString("event_id")))) {
                    UUID key = UUID.fromString(rs.getString("event_id"));
                    EventAndParticipants newElement = eventsAndParticipantsTable.get(key);
                    newElement.addParticipant(toParticipant(rs));
                    eventsAndParticipantsTable.replace(key, newElement);
                } else { //if the event is new
                    UUID key = UUID.fromString(rs.getString("event_id"));
                    List<Participant> participantList = new ArrayList<Participant>();
                    participantList.add(toParticipant(rs));
                    EventAndParticipants value =
                            EventAndParticipants.builder()
                                    .event(toEvent(rs))
                                    .participantList(participantList)
                                    .build();

                    eventsAndParticipantsTable.put(key, value);
                }
            }

            return eventsAndParticipantsTable;
        } catch (SQLException e) {
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
                .id(UUID.fromString(rs.getString("event_id")))
                .date(rs.getString("date"))
                .time(rs.getString("time"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .hostEmail(rs.getString("host_email"))
                .build();
    }

}