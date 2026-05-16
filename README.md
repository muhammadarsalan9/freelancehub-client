# FreelanceHub Client

A Core Java console application that consumes the 
[FreelanceHub REST API](https://github.com/muhammadarsalan9/freelancehub-api).
Demonstrates client-server communication using Java's built-in HttpClient.

## What it does

A menu-driven console program where you can:
- Register as CLIENT or FREELANCER
- Login and receive JWT token automatically
- Post jobs (CLIENT)
- Submit proposals (FREELANCER)
- View all jobs and open jobs
- Filter jobs by budget range
- Search users by name
- View proposals by freelancer
- Accept proposals (CLIENT)

## How it works

User selects menu option
→ Java HttpClient sends HTTP request to Spring Boot API
→ JWT token sent automatically in Authorization header
→ JSON response parsed and displayed in console

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Core language |
| Java HttpClient | Built-in HTTP requests |
| Jackson Databind | JSON parsing |
| Maven | Build tool |

## Prerequisites

- Java 17 installed
- [FreelanceHub API](https://github.com/muhammadarsalan9/freelancehub-api) 
  running on localhost:8080

## How to Run

**Step 1 — Clone this project:**
```bash
git clone https://github.com/muhammadarsalan9/freelancehub-client.git
cd freelancehub-client
```

**Step 2 — Start the FreelanceHub API first:**
```bash
# In the freelancehub-api folder
./mvnw spring-boot:run
```

**Step 3 — Run the consumer:**
```bash
./mvnw compile exec:java -Dexec.mainClass="org.example.FreelanceHubConsumer"
```

Or simply open in IntelliJ and run `FreelanceHubConsumer.java`

## Menu Options
═══════════ MAIN MENU ═══════════

Register
Login
View all open jobs
View all jobs
Post a job        (CLIENT only)
Submit a proposal (FREELANCER only)
View proposals by freelancer ID
Accept a proposal
Search users by name
Filter jobs by budget
Exit


## Project Structure
freelancehub-client/
├── src/main/java/
│   ├── ApiClient.java           # HTTP client — handles all API calls
│   └── FreelanceHubConsumer.java # Main menu — console interface
└── pom.xml

## Related Project

This client consumes the FreelanceHub REST API:
**[github.com/muhammadarsalan9/freelancehub-api](https://github.com/muhammadarsalan9/freelancehub-api)**
