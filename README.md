# Teledon Charity Management System

Teledon is a Java-based charity case management application developed for the Software Design and Programming Environments course. The project follows the Teledon assignment and evolves across multiple weekly milestones, from the domain model and persistence layer to client-server communication, ORM integration, REST services, JWT authentication and real-time updates.

## Features

- Charity case, donor, volunteer and donation domain model.
- Repository layer with JDBC and Hibernate ORM implementations.
- Java desktop client for the volunteer workflow.
- JSON socket-based client-server communication.
- gRPC communication layer.
- Spring Boot REST API for charity case management.
- JWT-based volunteer authentication.
- WebSocket/STOMP notifications for real-time charity case updates.
- React web client for listing, filtering and managing charity cases.

## Project Structure

```text
Teledon/
  RestServices/          Spring Boot REST API, security and WebSocket support
  TeledonClient/         Java desktop and service clients
  TeledonModel/          Domain model
  TeledonNetworking/     JSON and gRPC networking layer
  TeledonPersistence/    JDBC and Hibernate repositories
  TeledonServer/         Server implementations
teledon-web-client/      React + Vite web client
```

## Branch Organization

The project history is organized by weekly milestones:

| Branch | Milestone |
| --- | --- |
| `saptamana-3` | Domain classes and repository interfaces |
| `week-4` | JDBC repositories, SQLite integration and logging |
| `week-5` | Week 5 application requirements |
| `week-7` | Client-server architecture |
| `week-9` | gRPC communication |
| `week-10` | ORM repositories |
| `week-11` | REST services and REST client |
| `week-12` | REST client-server updates |
| `week-14` | JWT authentication and observer notifications |
| `main` | Latest stable version |

## Technologies

- Java
- Gradle
- Spring Boot
- Spring Security
- Spring WebSocket
- JWT
- SQLite
- Hibernate
- gRPC
- React
- Vite
- Axios
- STOMP over WebSocket

## Running the Backend

From the `Teledon` directory:

```bash
./gradlew :RestServices:bootRun
```

The REST API starts on `http://localhost:8080`.

## Running the Web Client

From the `teledon-web-client` directory:

```bash
npm install
npm run dev
```

The Vite development server starts on the port shown in the terminal, usually `http://localhost:5173`.

## Verification

Useful checks before publishing or submitting:

```bash
cd Teledon
./gradlew test

cd ../teledon-web-client
npm run build
```

## Repository Notes

Generated files such as Gradle caches, IDE metadata, build outputs and `node_modules` are intentionally ignored. The repository is meant to preserve all milestone branches and their original commit dates while using `main` as the primary branch for the latest version.
