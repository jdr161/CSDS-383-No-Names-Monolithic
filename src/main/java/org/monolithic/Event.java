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
public class Event {
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String date;

    private String time;

    private String title;

    private String description;

    private String hostEmail;
}
