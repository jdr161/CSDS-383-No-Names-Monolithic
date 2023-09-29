package org.monolithic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participant {
@Builder.Default
    private UUID participantId = UUID.randomUUID();

    private String participantName;

    private String participantEmail;



}
