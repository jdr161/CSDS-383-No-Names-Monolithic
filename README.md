# CSDS-383-No-Names-Monolithic
Using JDK version 19 (Amazon Corretto)

## Explanation of Project
The goal of this project was to develop an application using a monolithic architecture. The application had the following requirements:

- The client should be able to execute and interact with the application via a command line interface (CLI).
- The client should be able to create events.
  - Each event should have the following attributes:
    - Event Id: A UUID. If the UUID is not provided, the application should generate one for the
    - event.
    - Event Date
      - The event date must follow the format: YYYY-MM-DD
    - Event Time: Date of the event.
      - The event time must follow the format: HH:MM AM/PM 
    - Event Title: Title of the event.
      - The event title should not be longer than 255 characters.
    - Event Description: Description of the event.
    - The event title should not be longer than 600 characters.
    - Event Host Email: The email of the host of the event.
      - The event host's email should be valid.
      - Invalid emails should be rejected.
- The client should be able to register participants to events.
  - Each event participant should have the following attributes:
    - Event Participant Id:  A UUID. If the UUID is not provided, the application should generate one for the participant.
    - Event Id: Id of the event to which the participant is registered to.
    - Event participant name: Name of the event participant.
      - The name of the event participant should not be longer than 600 characters.
    - Event participant email: Email of the event participant.
      - The event participant's email should be valid string (format). No need to authenticate.
      - Invalid emails should be rejected.
- All events and event participants should be stored in a database (relational databases are recommended, but any database works for this exercise).
- The client should be able to list all the events and event participants available stored in the system (database).
- The application should be monolithic and run locally (no need for the customers to deploy this application).
- The application should be able to be executed in a Unix-based or Windows environment.

## How to run the project
There are two ways to run our project: through the .jar file or by building the project. Both methods have the same functional result. The .jar file was created by exporting the project present in this repo.

### How to Run the Jar file
To run the .jar file, open a terminal at the root of the project directory. Before running the file, ensure that you have the correct JDK--version 19 (Amazon Corretto)--using:
```
java -version
```
Then, run the .jar file using:
```
java -jar CSDS-383-No-Names-Monolithic.jar
```

You should see the command line interface at that point.


### How to build and run the project
To build and run the application from its GitHub source, we simply use the IntelliJ IDEA (free community edition) IDE. With the root repository project opened in IntelliJ, the project can be built by clicking the "Build" menu item button and then clicking "Build Project". It can also be run by clicking the "Run" menu item button and then clicking "Run Main.java".
