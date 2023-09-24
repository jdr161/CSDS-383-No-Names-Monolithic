package org.monolithic;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface EventDao {
    List<Event> getAllEvents() throws SQLException;
    Event getEventById(UUID id) throws SQLException;
    void addEvent(Event event) throws SQLException;
}
