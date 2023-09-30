package org.monolithic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.monolithic.Main.conn;

/**
 * Allows the client to register participants to an event
 *
 * @author Justin Kocur
 */
public class ParticipantDao {

    public static void addParticipant(Participant participant) throws SQLException {
        final String INSERT_PARTICIPANT_SQL = """
                INSERT INTO participants (id ,name, email) 
                VALUES (?, ?, ?);""";

        try (PreparedStatement preparedStatement = conn.prepareStatement(INSERT_PARTICIPANT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setObject(1, participant.getParticipantId()); // MIGHT NEED TO CHANGE LATER
            preparedStatement.setString(2, participant.getParticipantName());
            preparedStatement.setString(3, participant.getParticipantEmail());
            preparedStatement.executeUpdate();

            ResultSet res = preparedStatement.getGeneratedKeys();
            res.next(); // TODO throw if no next.
        }
    }

    public static List<Participant> getAllParticipants() {
        List<Participant> participantsList = new ArrayList<>();
        final String GET_ALL_PARTICIPANTS_SQL = "SELECT * FROM participants";

        try (Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery(GET_ALL_PARTICIPANTS_SQL);

            while (rs.next()) {
                participantsList.add(toParticipant(rs));
                //Another loop here to go through participants for each participant
                //Pull participants for each partivipant: final String GET_ALL_PARTICIPANTS_SQL = "SELECT * FROM participants";
                //
            }

            return participantsList;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve participants", e);
        }
    }

    private static Participant toParticipant(ResultSet rs) throws SQLException {
        if (rs == null) {
            return null;
        }

        return Participant.builder()
                .participantId(UUID.fromString(rs.getString("id")))
                .participantName(rs.getString("name"))
                .participantEmail(rs.getString("email"))
                .build();
    }

    public static boolean doesParticipantExist(String participantId) {
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

    public static void addParticipantInEvent(String participantId, String eventId) throws SQLException {
        final String INSERT_PARTICIPANT_IN_EVENT_SQL = """
                INSERT INTO participant_in_event (participant_id, event_id) 
                VALUES (?, ?);""";

        try (PreparedStatement preparedStatement = conn.prepareStatement(INSERT_PARTICIPANT_IN_EVENT_SQL)) {
            preparedStatement.setString(1, participantId);
            preparedStatement.setString(2, eventId);
            preparedStatement.executeUpdate();
        }
    }
}