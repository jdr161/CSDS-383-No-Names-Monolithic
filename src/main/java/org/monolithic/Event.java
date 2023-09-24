package org.monolithic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class Event {
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NonNull
    private String date;

    @NonNull
    private String time;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    private String hostEmail;
}
