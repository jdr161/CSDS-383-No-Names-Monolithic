package org.monolithic;

import java.sql.SQLException;
import java.util.*;

public class MainCli {
    private static final Scanner scanner = new Scanner(System.in);

    private static void printMenuOptions(String[] options) {
        for (String option : options) {
            System.out.println(option);
        }
        System.out.println("---------------------------");
        System.out.print("Select an option: ");
    }

    private static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", "cls"});
            } else {
                Runtime.getRuntime().exec(new String[]{"clear"});
            }
        } catch (Exception ignored) {
        }
    }

    public static CliCode mainMenu() {
        try {
            String[] options = {"\n --- Main Menu ---",
                    "[1] View All Events",
                    "[2] Create an Event",
                    "[3] Create Participants",
                    "[4] View All Participants",
                    "[5] Register a Participant to an Event",
                    "[6] Exit Program"
            };
            printMenuOptions(options);

            int input = scanner.nextInt();
            if (input < 1 || input > 6)
                throw new InputMismatchException("Incorrect input given");

            clearConsole();

            switch (input) {
                // View all events
                case 1 -> {
                    return handleViewAllEventsRequest();
                }
                // Create an event
                case 2 -> {
                    return handleCreateEventRequest();
                }
                // Create participants
                case 3 -> {
                    return handleCreateParticipantRequest();
                }
                // View all participants
                case 4 -> {
                    return handleViewAllParticipantsRequest();
                }
                // Register a participant to event
                case 5 -> {
                    return handleRegisterParticipantRequest();
                }
                // Exit program
                default -> {
                    scanner.close();
                    return CliCode.NO_ERROR_NO_REPEAT_OP;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return CliCode.MAIN_MENU;
        }
    }

    //Option 1: View all events
    private static CliCode handleViewAllEventsRequest() {
        Hashtable<UUID, EventAndParticipants> eventsAndParticipantsTable = EventAndParticipantsDao.getAllEventsAndParticipants();
        Collection<EventAndParticipants> values = eventsAndParticipantsTable.values();

        if (eventsAndParticipantsTable.size() == 0) {
            System.out.println("No available events. Returning to main menu");
            System.out.println("--------------");
        } else {

            System.out.println("--------------");
            System.out.println("Events and Participants");
            System.out.println("--------------");
            System.out.printf("| %-36s | %-10s | %-8s | %-50s | %-50s | %-20s |%n", "ID", "Date", "Time", "Title", "Description", "Host Email");
            System.out.println("--------------");
            for (EventAndParticipants eventAndParticipants : values) {
                Event e = eventAndParticipants.getEvent();
                System.out.printf("| %-36s | %-10s | %-8s | %-50s | %-50s | %-20s |%n",
                        e.getId().toString(), e.getDate(), e.getTime(), e.getTitle().replaceAll(".{80}(?=.)", "$0\n"),
                        e.getDescription().replaceAll(".{80}(?=.)", "$0\n"), e.getHostEmail());
                if(eventAndParticipants.getParticipantList().isEmpty()){
                    System.out.println("--- this event has no participants");
                } else {
                    System.out.printf("--- | %-36s | %-10s | %-8s |%n", "ID", "Name", "Email");
                    for (Participant p : eventAndParticipants.getParticipantList()) {
                        System.out.printf("--- | %-36s | %-10s | %-8s |%n",
                                p.getParticipantId().toString(), p.getParticipantName(), p.getParticipantEmail());
                    }
                }
            }
            System.out.println("--------------");
            System.out.println("[*] Retrieved all events");
        }

        return CliCode.MAIN_MENU;
    }

    //Handles the creation of a new event as well as the validation of the input
    private static CliCode handleCreateEventRequest() throws SQLException {
        Event event = Event.builder().build();
        scanner.nextLine();

        System.out.println("--- New event ---");
        System.out.println("[*] Press 'C' or 'c' and then ENTER at any input prompt to cancel");

        CliCode result = CliInputHandlerUtils.handleUuidCreationInput("Set a UUID for the event, or press ENTER for an auto-generated one: ", event);
        if (result != CliCode.CONTINUE) return CliCode.MAIN_MENU;

        result = CliInputHandlerUtils.handleEventDateInput(event);
        if (result != CliCode.CONTINUE) return CliCode.MAIN_MENU;

        result = CliInputHandlerUtils.handleEventTimeInput(event);
        if (result != CliCode.CONTINUE) return CliCode.MAIN_MENU;

        result = CliInputHandlerUtils.handleEventTitleInput(event);
        if (result != CliCode.CONTINUE) return CliCode.MAIN_MENU;

        result = CliInputHandlerUtils.handleEventDescriptionInput(event);
        if (result != CliCode.CONTINUE) return CliCode.MAIN_MENU;

        result = CliInputHandlerUtils.handleEmailInput("Enter the email of the event host: ", event);
        if (result != CliCode.CONTINUE) return CliCode.MAIN_MENU;

        EventDao.addEvent(event);
        System.out.println("[*] Successfully added event. Returning to main menu");
        return CliCode.MAIN_MENU;
    }

    private static CliCode handleViewAllParticipantsRequest() {
        List<Participant> participants = ParticipantDao.getAllParticipants();

        if (participants == null || participants.isEmpty()) {
            System.out.println("No participants. Returning to main menu");
            System.out.println("--------------");
        } else {
            System.out.println("--------------");
            System.out.println("Participants");
            System.out.println("--------------");
            System.out.printf("| %-36s | %-10s | %-8s |%n", "ID", "Name", "Email");
            System.out.println("--------------");
            for (Participant e : participants) {
                System.out.printf("| %-36s | %-10s | %-8s |%n",
                        e.getParticipantId().toString(), e.getParticipantName(), e.getParticipantEmail());
            }
            System.out.println("--------------");
            System.out.println("[*] Retrieved all participants");
        }
        return CliCode.MAIN_MENU;
    }

    // Option 3: Register participants
    private static CliCode handleCreateParticipantRequest() throws SQLException {
        Participant participant = Participant.builder().build();
        scanner.nextLine();

        System.out.println("--- New participant ---");
        System.out.println("[*] Press 'C' or 'c' and then ENTER at any input prompt to cancel");

        CliCode result = CliInputHandlerUtils.handleUuidCreationInput("Set a UUID for the participant, or press ENTER for an auto-generated one: ", participant);
        if (result != CliCode.CONTINUE) return CliCode.MAIN_MENU;

        result = CliInputHandlerUtils.handleParticipantNameInput(participant);
        if (result != CliCode.CONTINUE) return CliCode.MAIN_MENU;

        result = CliInputHandlerUtils.handleEmailInput("Enter email of participant: ", participant);
        if (result != CliCode.CONTINUE) return CliCode.MAIN_MENU;

        ParticipantDao.addParticipant(participant);
        System.out.println("[*] Successfully added partıcıpant. Returning to main menu");
        return CliCode.MAIN_MENU;
    }

    // TODO refactor, ugly validation
    private static CliCode handleRegisterParticipantRequest() {
        boolean validInput = false;
        scanner.nextLine();

        System.out.println("[*] Press 'C' or 'c' and then ENTER at any input prompt to cancel");

        String participantUuidInput = null;
        while (!validInput) {
            try {
                System.out.print("Enter the UUID for the participant to register: ");
                participantUuidInput = scanner.nextLine();

                if (isCancelRequest(participantUuidInput)) {
                    return CliCode.MAIN_MENU;
                }
                if (participantUuidInput == null || participantUuidInput.isBlank()) {
                    System.out.println("Input must be a valid UUID. Try again");
                    continue;
                }

                UUID.fromString(participantUuidInput);

                if (!ParticipantDao.doesParticipantExist(participantUuidInput)) {
                    System.out.println("Participant UUID doesn't exist. Try again");
                    continue;
                }

                validInput = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Input must be a valid UUID. Try again");
            }
        }

        String eventUuidInput = null;
        validInput = false;
        while (!validInput) {
            try {
                System.out.print("Enter the UUID for the event to register to: ");
                eventUuidInput = scanner.nextLine();

                if (isCancelRequest(eventUuidInput)) {
                    return CliCode.MAIN_MENU;
                }
                if (eventUuidInput == null || eventUuidInput.isBlank()) {
                    System.out.println("Input must be a valid UUID. Try again");
                    continue;
                }

                UUID.fromString(eventUuidInput);

                if (!EventDao.doesEventExist(eventUuidInput)){
                    System.out.println("Event UUID doesn't exist.");
                    continue;
                }

                validInput = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Input must be a valid UUID. Try again");
            }
        }

        try {
            ParticipantDao.addParticipantInEvent(participantUuidInput, eventUuidInput);
        } catch (SQLException e) { // should be unexpected at this point.
            throw new RuntimeException(e);
        }

        System.out.println("[*] Successfully registered participant to event. Returning to main menu");
        return CliCode.MAIN_MENU;
    }

    public static boolean isCancelRequest(String input) {
        if (input != null && input.equalsIgnoreCase("c")) {
            System.out.println("[*] Cancel request successful");
            return true;
        }
        return false;
    }
}
