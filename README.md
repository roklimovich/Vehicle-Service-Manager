# Vehicle Service Manager

A Java console application for managing a vehicle service centre. Developed as a **MAS (Modelling and Analysis of Information Systems)** university project at the Polish-Japanese Academy of Information Technology (PJATK).

The system models the core domain of an automotive service station — vehicles, clients, service orders, and mechanics — with full persistence via Hibernate ORM.

---

## Tech Stack

| Layer       | Technology               |
|-------------|--------------------------|
| Language    | Java 22                  |
| ORM         | Hibernate 7.0.0.Final    |
| Database    | H2 (in-memory) / SQLite  |
| Build tool  | Maven                    |

---

## Prerequisites

- **Java 22** or newer
- **Maven 3.6+**

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/roklimovich/Vehicle-Service-Manager.git
cd Vehicle-Service-Manager
```

### 2. Build the project

```bash
mvn clean install
```

### 3. Run the application

```bash
mvn exec:java -Dexec.mainClass="pl.pja.edu.s27619.Main"
```

Or run the packaged JAR:

```bash
mvn clean package
java -jar target/MAS_Project1-1.0-SNAPSHOT.jar
```

---

## Project Structure

```
Vehicle-Service-Manager/
├── src/
│   └── main/
│       ├── java/pl/pja/edu/s27619/
│       │   ├── Main.java              # Application entry point
│       │   ├── model/                 # Domain entities (Vehicle, Client, Order, ...)
│       │   ├── repository/            # Hibernate-based data access
│       │   └── service/               # Business logic layer
│       └── resources/
│           └── hibernate.cfg.xml      # Hibernate & database configuration
├── pom.xml
└── README.md
```

---

## Database Configuration

The application supports two database backends, configured in `src/main/resources/hibernate.cfg.xml`.

**H2** (in-memory, resets on every run — good for development):
```xml
<property name="hibernate.dialect">org.hibernate.dialect.H2Dialect</property>
<property name="hibernate.connection.url">jdbc:h2:mem:vehicledb;DB_CLOSE_DELAY=-1</property>
<property name="hibernate.connection.driver_class">org.h2.Driver</property>
```

**SQLite** (file-based, data persists between runs):
```xml
<property name="hibernate.dialect">org.hibernate.community.dialect.SQLiteDialect</property>
<property name="hibernate.connection.url">jdbc:sqlite:vehicle_service.db</property>
<property name="hibernate.connection.driver_class">org.sqlite.JDBC</property>
```

Set `hibernate.hbm2ddl.auto` to `create` on first run to generate the schema, then switch to `update` to preserve data.

---

## Domain Model

| Entity           | Description                                            |
|------------------|--------------------------------------------------------|
| **Client**       | Registered customer of the service centre             |
| **Vehicle**      | A car or motorcycle linked to a client                |
| **ServiceOrder** | A repair or maintenance request for a vehicle         |
| **Mechanic**     | An employee assigned to carry out service work        |
| **ServiceTask**  | An individual task performed within a service order   |

---

## Running Tests

```bash
mvn test
```

---
