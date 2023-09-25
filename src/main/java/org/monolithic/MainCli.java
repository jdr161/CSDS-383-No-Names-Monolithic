package org.monolithic;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

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
                    "[3] Register Participants",
                    "[4] Exit Program"
            };
            printMenuOptions(options);

            int input = scanner.nextInt();
            if (input < 1 || input > 4)
                throw new InputMismatchException("Incorrect input given");

            clearConsole();

            switch (input) {
                case 1 -> {
                    return handleViewAllEventsRequest();
                }
                case 2 -> {
                    // TODO
                    return CliCode.MAIN_MENU;
                }
                case 3 -> {
                    // TODO
                    return CliCode.MAIN_MENU;
                }
                case 4 -> {
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
            System.out.println("Retrieved all events");
            System.out.println("--------------");
        }

        return CliCode.MAIN_MENU;
    }
}
