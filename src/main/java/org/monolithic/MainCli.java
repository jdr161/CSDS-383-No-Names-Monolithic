package org.monolithic;

import org.monolithic.utils.FormatUtils;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

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
                    "[5] Exit Program"
            };
            printMenuOptions(options);

            int input = scanner.nextInt();
            if (input < 1 || input > 5)
                throw new InputMismatchException("Incorrect input given");

            clearConsole();

            switch (input) {
                //View all events
                case 1 -> {
                    return handleViewAllEventsRequest();
                }
                //Create an event
                case 2 -> {
                    return handleCreateEventRequest();
                }
                // Create participants
                case 3 -> {
                    return handleCreateParticipantRequest();
                }
                //View all participants
                case 4 -> {
                    return handleViewAllParticipantsRequest();
                }
                //Exit program
                case 5 -> {
                    return CliCode.NO_ERROR_NO_REPEAT_OP;
                }
                default -> System.exit(0);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return CliCode.MAIN_MENU;
        }

        scanner.close();
        return CliCode.NO_ERROR_NO_REPEAT_OP;
    }

    //Option 1: View all events
    private static CliCode handleViewAllEventsRequest() {
        EventDao eventDao = new EventDao();
        List<Event> events = eventDao.getAllEvents();

        if (events == null || events.isEmpty()) {
            System.out.println("No available events. Returning to main menu");
            System.out.println("--------------");
        } else {
            System.out.println("--------------");
            System.out.println("Events");
            System.out.println("--------------");
            System.out.printf("| %-36s | %-10s | %-8s | %-50s | %-50s | %-20s |%n", "ID", "Date", "Time", "Title", "Description", "Host Email");
            System.out.println("--------------");
            for (Event e : events) {
                System.out.printf("| %-36s | %-10s | %-8s | %-50s | %-50s | %-20s |%n",
                        e.getId().toString(), e.getDate(), e.getTime(), e.getTitle().replaceAll(".{80}(?=.)", "$0\n"),
                        e.getDescription().replaceAll(".{80}(?=.)", "$0\n"), e.getHostEmail());
            }
            System.out.println("--------------");
            System.out.println("[*] Retrieved all events");
        }

        return CliCode.MAIN_MENU;
    }

    //Option 2: Create an event
    private static CliCode handleCreateEventRequest() throws SQLException {
        Event event = Event.builder().build();
        boolean validInput = false;
        boolean retry = false;

        System.out.println("--- New event ---");
        System.out.println("[*] Press 'C' or 'c' and then ENTER at any input prompt to cancel");

        System.out.print("Set a UUID for the event, press ENTER for an auto-generated one: ");
        scanner.nextLine();
        while (!validInput) {
            try {
                if (retry) {
                    System.out.print("Set a UUID for the event, press ENTER for an auto-generated one: ");
                }
                String uuidInput = scanner.nextLine();
                CliCode cancelFlag = checkForAndHandleCancel(uuidInput);
                if (cancelFlag == CliCode.MAIN_MENU) {
                    return cancelFlag;
                }
                if (uuidInput != null && !uuidInput.isBlank()) {
                    event.setId(UUID.fromString(uuidInput));
                }
                validInput = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Input must be a valid UUID. Try again");
                retry = true;
            }
        }

        validInput = false;
        retry = false;
        System.out.print("Set a date: ");
        while (!validInput) {
            try {
                if (retry) {
                    System.out.print("Set a date: ");
                }
                String dateInput = scanner.nextLine();
                CliCode cancelFlag = checkForAndHandleCancel(dateInput);
                if (cancelFlag == CliCode.MAIN_MENU) {
                    return cancelFlag;
                }
                event.setDate(FormatUtils.formatDate(dateInput));
                validInput = true;
            } catch (IllegalArgumentException | ParseException e) {
                System.out.println("Input must match the format YYYY-MM-DD or be parsable into the specified format. Try again");
                retry = true;
            }
        }

        validInput = false;
        retry = false;
        System.out.print("Set a time: ");
        while (!validInput) {
            try {
                if (retry) {
                    System.out.print("Set a time: ");
                }
                String timeInput = scanner.nextLine();
                CliCode cancelFlag = checkForAndHandleCancel(timeInput);
                if (cancelFlag == CliCode.MAIN_MENU) {
                    return cancelFlag;
                }
                event.setTime(FormatUtils.formatTime(timeInput));
                validInput = true;
            } catch (IllegalArgumentException | ParseException e) {
                System.out.println("Input must match the format HH:MM AM/PM or be parsable into the specified format. Try again");
                retry = true;
            }
        }

        validInput = false;
        retry = false;
        System.out.print("Set a title: ");
        while (!validInput) {
            try {
                if (retry) {
                    System.out.print("Set a title: ");
                }
                String title = scanner.nextLine();
                CliCode cancelFlag = checkForAndHandleCancel(title);
                if (cancelFlag == CliCode.MAIN_MENU) {
                    return cancelFlag;
                }

                if (title == null || title.length() < 1 || title.length() > 255) {
                    throw new IllegalArgumentException("Title should be between 1 and 255 characters, inclusive. Try again");
                } else {
                    event.setTitle(title);
                    validInput = true;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                retry = true;
            }

        }

        validInput = false;
        retry = false;
        System.out.print("Set a description: ");
        while (!validInput) {
            try {
                if (retry) {
                    System.out.print("Set a description: ");
                }
                String description = scanner.nextLine();
                CliCode cancelFlag = checkForAndHandleCancel(description);
                if (cancelFlag == CliCode.MAIN_MENU) {
                    return cancelFlag;
                }

                if (description == null || description.length() < 1 || description.length() > 600) {
                    throw new IllegalArgumentException("Title should be between 1 and 600 characters, inclusive. Try again");
                } else {
                    event.setDescription(description);
                    validInput = true;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                retry = true;
            }
        }

        validInput = false;
        retry = false;
        System.out.print("Set a host email: ");
        while (!validInput) {
            try {
                if (retry) {
                    System.out.print("Set a host email: ");
                }
                String hostEmail = scanner.nextLine();
                CliCode cancelFlag = checkForAndHandleCancel(hostEmail);
                if (cancelFlag == CliCode.MAIN_MENU) {
                    return cancelFlag;
                }

                if (!FormatUtils.isValidEmail(hostEmail)) {
                    throw new IllegalArgumentException("Invalid email. Try again");
                } else {
                    event.setHostEmail(hostEmail);
                    validInput = true;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                retry = true;
            }
        }
        EventDao eventDao = new EventDao();
        eventDao.addEvent(event);
        System.out.println("[*] Successfully added event. Returning to main menu");
        return CliCode.MAIN_MENU;
    }

    //Option 3: Register participants
    private static CliCode handleCreateParticipantRequest() throws SQLException {
        Participant participant = Participant.builder().build();
        boolean validInput = false;
        boolean retry = false;

        System.out.println("--- New participant ---");
        System.out.println("[*] Press 'C' or 'c' and then ENTER at any input prompt to cancel");

        System.out.print("Set a UUID for the participant, press ENTER for an auto-generated one: ");
        scanner.nextLine();
        while (!validInput) {
            try {
                if (retry) {
                    System.out.print("Set a UUID for the participant, press ENTER for an auto-generated one: ");
                }
                String uuidInput = scanner.nextLine();
                CliCode cancelFlag = checkForAndHandleCancel(uuidInput);
                if (cancelFlag == CliCode.MAIN_MENU) {
                    return cancelFlag;
                }
                if (uuidInput != null && !uuidInput.isBlank()) {
                    participant.setParticipantId(UUID.fromString(uuidInput));
                }
                validInput = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Input must be a valid UUID. Try again");
                retry = true;
            }
        }

        validInput = false;
        retry = false;


        System.out.println("Enter Participant Name: ");
        while (!validInput) {
            try {
                if (retry) {
                    System.out.println("Enter Participant Name: ");
                }
                String participantName = scanner.nextLine();
                CliCode cancelFlag = checkForAndHandleCancel(participantName);
                if (cancelFlag == CliCode.MAIN_MENU) {
                    return cancelFlag;
                }

                if (participantName == null || participantName.length() < 1 || participantName.length() > 600) {
                    throw new IllegalArgumentException("Participant name should be between 1 and 600 characters, inclusive. Try again");
                } else {
                    participant.setParticipantName(participantName);
                    validInput = true;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                retry = true;
            }

        }

        validInput = false;
        retry = false;
        System.out.println("Enter Participant Email: ");
        while (!validInput) {
            try {
                if (retry) {
                    System.out.println("Enter Participant Email: ");
                }
                String participantEmail = scanner.nextLine();
                CliCode cancelFlag = checkForAndHandleCancel(participantEmail);
                if (cancelFlag == CliCode.MAIN_MENU) {
                    return cancelFlag;
                }

                if (!FormatUtils.isValidEmail(participantEmail)) {
                    throw new IllegalArgumentException("Invalid email. Try again");
                } else {
                    participant.setParticipantEmail(participantEmail);
                    validInput = true;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                retry = true;
            }
        }

        ParticipantDao.addParticipant(participant);
        System.out.println("[*] Successfully added partıcıpant. Returning to main menu");
        return CliCode.MAIN_MENU;
    }

    private static CliCode handleViewAllParticipantsRequest() {
        List<Participant> participants = ParticipantDao.getAllParticipants();

        if (participants == null || participants.isEmpty()) {
            System.out.println("No participants. Returning to main menu");
            System.out.println("--------------");
        } else {
            System.out.println("--------------");
            System.out.println("Particpants");
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

    private static CliCode checkForAndHandleCancel(String input) {
        if (input != null && input.equalsIgnoreCase("c")) {
            System.out.println("[*] Cancel request successful");
            return CliCode.MAIN_MENU;
        }
        return CliCode.CONTINUE;
    }
}
