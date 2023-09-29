package org.monolithic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

}