# CLI Interpreter
A simple Java-based Command Line Interpreter. It mimics basic Unix/Linux shell functionality.

## Features
- Supports common commands: `pwd`, `cd`, `ls`, `mkdir`, `rm`, `mv`, `cat`, `>`, `>>`, `|`
- Built-in commands: `help`, `exit`
- Graceful error handling

## Testing
- Fully tested using **JUnit**
- Includes test cases for each command

## Tech Stack
- Java
- JUnit 5
- Maven

## How to Run
```bash
mvn compile
mvn exec:java
```
## Run tests
```
mvn test
```
