package org.monolithic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class EventAndParticipants {
    @Builder.Default
    private Event event;
    private List<Participant> participantList;

    public void addParticipant(Participant p){
        participantList.add(p);
    }
}
