package org.monolithic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.UUID;

import static org.monolithic.Main.conn;

/**
 * Allows the client to register participants to an event
 *
 * @author Justin Kocur
 */
public class ParticipantDao {

    public static void register(Participant participant) throws SQLException {
        registerIndividual(participant.getParticipantId(), participant.getEventId(), participant.getParticipantName(), participant.getParticipantEmail());
    }

    /**
     * Allows the client to register participants to an event
     *
     * @param participantId         A UUID. If the UUID is not provided, a UUID will be generated for the participant
     * @param eventId               A UUID. Links to an external site. If the UUID is not provided, a UUID will be generated for the event
     * @param participantName       Name of the event participant, limited to 600 characters
     * @param participantEmail      Email of the event participant.  Invalid emails will be rejected
     */
    private static void registerIndividual(UUID participantId, UUID eventId, String participantName, String participantEmail) throws SQLException {
        // Invalid input detected
        if(!isValidInputs(participantId, eventId, participantName, participantEmail))
            return;

        participantName = getParticipantName(participantName);          // 'participantName' may be too long

        final String INSERT_PARTICIPANT_SQL = """
                INSERT INTO participants (event_id, name, email) 
                VALUES (?, ?, ?);""";

        System.out.println("My ID: " + eventId);
        try (PreparedStatement preparedStatement = conn.prepareStatement(INSERT_PARTICIPANT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setObject(1, eventId); // MIGHT NEED TO CHANGE LATER
            preparedStatement.setString(2, participantName);
            preparedStatement.setString(3, participantEmail);
            preparedStatement.executeUpdate();

            ResultSet res = preparedStatement.getGeneratedKeys();
            res.next(); // TODO throw if no next.
        }
    }

    /**
     *  Cuts off String input if too long
     *
     * @param participantName       the participant's given name
     * @return                      the participant's name in the system
     */
    private static String getParticipantName(String participantName) {
        return participantName.length() > 600 ? participantName.substring(0, 601) : participantName;
    }

    /**
     *  Returns true if email address given is valid, false if not
     *
     * @param participantEmail      the given email address to validate
     * @return                      whether the email address is valid
     */
    private static boolean validEmail(String participantEmail) {
        // Might need to define what a 'valid' email address is

        // Check for '@'
        int atCount = 0;
        for(int i = 0; i < participantEmail.length(); i++) {
            if(participantEmail.charAt(i) == '@')
                atCount++;
        }

        // Check for '.'
        int dotCount = 0;
        for(int i = 0; i < participantEmail.length(); i++) {
            if(participantEmail.charAt(i) == '.')
                dotCount++;
        }

        return atCount == 1 && dotCount == 1;
    }

    /**
     *  Determines if inputs are valid, based on assignment directions
     *
     * @param participantId         the participantId given
     * @param eventId               the eventId given
     * @param participantName       the participantName given
     * @param participantEmail      the participantEmail given
     * @return                      whether inputs are valid
     */
    private static boolean isValidInputs(UUID participantId, UUID eventId, String participantName, String participantEmail) {
        // These null inputs are on the internal/ coder side
        Objects.requireNonNull(participantId);
        Objects.requireNonNull(eventId);
        Objects.requireNonNull(participantName);
        Objects.requireNonNull(participantEmail);

        /* These next few checks are the external/ user's faults */

        // Invalid email format
        if(!validEmail(participantEmail))
            return false;

        return true;
    }
}