# Homework 10: Behavioral Design Patterns (Mediator, Memento, Iterator)

A Java chat app where users send messages to each other through a central server.
It uses three behavioral design patterns:

- **Mediator**: users never talk to each other directly. Every message goes through
  `ChatServer`, which also decides who has blocked whom.
- **Memento**: every message a user sends is saved as a snapshot (`MessageMemento`),
  so the user can undo the last message they sent.
- **Iterator**: a user can step through their chat history one message at a time,
  filtered to the messages sent to or from a specific user.

Tested with JUnit 5 and Maven.

## Requirements

- Java 17 or newer
- Maven 3.9 or newer

## Run the tests

```
mvn test
```

In VS Code you can also run them from the Testing panel.

## Run the driver

```
mvn -q compile
java -cp target/classes chatapp.ChatDriver
```

## Project structure

```
src/main/java/chatapp/
  Message.java          one message: sender, recipients, timestamp, content (Memento: originator)
  MessageMemento.java   snapshot of a sent message: content + timestamp (Memento: memento)
  ChatHistory.java      one user's messages + undo snapshots (Memento: caretaker)
  ChatServer.java       registers users, routes messages, blocks, undoes (Mediator: mediator)
  User.java             sends, receives, undoes, blocks, only through the server (Mediator: colleague)
  ChatDriver.java       demo with 3 users
```
