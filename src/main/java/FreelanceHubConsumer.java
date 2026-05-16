package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FreelanceHubConsumer {

    private static final ApiClient apiClient = new ApiClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Scanner scanner = new Scanner(System.in);
    private static String loggedInUserRole = "";

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     FreelanceHub Console Client      ║");
        System.out.println("║   Connecting to localhost:8080       ║");
        System.out.println("╚══════════════════════════════════════╝");

        while (true) {
            showMainMenu();
        }
    }

    static void showMainMenu() {
        System.out.println("\n═══════════ MAIN MENU ═══════════");
        if (apiClient.getAuthToken() != null) {
            System.out.println("Logged in as: " + loggedInUserRole);
        }
        System.out.println("1.  Register");
        System.out.println("2.  Login");
        System.out.println("3.  View all open jobs");
        System.out.println("4.  View all jobs");
        System.out.println("5.  Post a job        (CLIENT only)");
        System.out.println("6.  Submit a proposal (FREELANCER only)");
        System.out.println("7.  View proposals by freelancer ID");
        System.out.println("8.  Accept a proposal");
        System.out.println("9.  Search users by name");
        System.out.println("10. Filter jobs by budget");
        System.out.println("0.  Exit");
        System.out.print("\nEnter your choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1"  -> register();
            case "2"  -> login();
            case "3"  -> viewOpenJobs();
            case "4"  -> viewAllJobs();
            case "5"  -> postJob();
            case "6"  -> submitProposal();
            case "7"  -> viewMyProposals();
            case "8"  -> acceptProposal();
            case "9"  -> searchUsers();
            case "10" -> filterByBudget();
            case "0"  -> { System.out.println("Goodbye!"); System.exit(0); }
            default   -> System.out.println("Invalid choice.");
        }
    }

    // ── pause helper ──────────────────────────────────
    static void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // ── 1. Register ───────────────────────────────────
    static void register() {
        System.out.println("\n─── REGISTER ───");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Role (CLIENT / FREELANCER): ");
        String role = scanner.nextLine().trim().toUpperCase();

        try {
            Map<String, String> body = new HashMap<>();
            body.put("name", name);
            body.put("email", email);
            body.put("password", password);
            body.put("role", role);

            String response = apiClient.post("/api/users/register", body);
            JsonNode json = mapper.readTree(response);

            if (json.has("id")) {
                System.out.println("\n✅ Registered successfully!");
                System.out.println("Your ID:   " + json.get("id").asLong());
                System.out.println("Name:      " + json.get("name").asText());
                System.out.println("Email:     " + json.get("email").asText());
                System.out.println("Role:      " + json.get("role").asText());
                System.out.println("Save your ID — you will need it later!");
            } else {
                System.out.println("\n❌ Registration failed:");
                System.out.println(apiClient.prettyPrint(response));
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        pause();
    }

    // ── 2. Login ──────────────────────────────────────
    static void login() {
        System.out.println("\n─── LOGIN ───");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            Map<String, String> body = new HashMap<>();
            body.put("email", email);
            body.put("password", password);

            String response = apiClient.post("/api/auth/login", body);
            JsonNode json = mapper.readTree(response);

            if (json.has("token")) {
                apiClient.setAuthToken(json.get("token").asText());
                loggedInUserRole = json.get("role").asText();
                System.out.println("\n✅ Login successful!");
                System.out.println("Welcome: " + json.get("name").asText());
                System.out.println("Role:    " + loggedInUserRole);
                System.out.println("Token:   saved ✅");
            } else {
                System.out.println("\n❌ Login failed:");
                System.out.println(apiClient.prettyPrint(response));
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        pause();
    }

    // ── 3. View open jobs ─────────────────────────────
    static void viewOpenJobs() {
        System.out.println("\n─── OPEN JOBS ───");
        try {
            String response = apiClient.getWithAuth("/api/jobs/open?page=0&size=10");
            JsonNode json = mapper.readTree(response);
            printJobs(json);
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        pause();
    }

    // ── 4. View all jobs ──────────────────────────────
    static void viewAllJobs() {
        System.out.println("\n─── ALL JOBS ───");
        try {
            String response = apiClient.getWithAuth("/api/jobs?page=0&size=10");
            JsonNode json = mapper.readTree(response);
            printJobs(json);
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        pause();
    }

    // ── jobs printer helper ───────────────────────────
    static void printJobs(JsonNode json) {
        // handle paginated response
        JsonNode jobs = json.has("content") ? json.get("content") : json;

        if (jobs.isEmpty()) {
            System.out.println("No jobs found.");
            return;
        }

        int total = json.has("totalElements") ?
                json.get("totalElements").asInt() : jobs.size();
        System.out.println("Total jobs: " + total);
        System.out.println("─────────────────────────────────────────");

        for (JsonNode job : jobs) {
            System.out.println("ID:        " + job.get("id").asLong());
            System.out.println("Title:     " + job.get("title").asText());
            System.out.println("Budget:    $" + job.get("budget").asDouble());
            System.out.println("Status:    " + job.get("status").asText());
            System.out.println("Client:    " + job.get("clientName").asText());
            System.out.println("─────────────────────────────────────────");
        }
    }

    // ── 5. Post a job ─────────────────────────────────
    static void postJob() {
        System.out.println("\n─── POST A JOB ───");
        if (apiClient.getAuthToken() == null) {
            System.out.println("❌ Please login first.");
            pause();
            return;
        }
        System.out.print("Job Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        System.out.print("Budget ($): ");
        double budget = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Your Client ID: ");
        long clientId = Long.parseLong(scanner.nextLine().trim());

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("title", title);
            body.put("description", description);
            body.put("budget", budget);
            body.put("clientId", clientId);

            String response = apiClient.postWithAuth("/api/jobs", body);
            JsonNode json = mapper.readTree(response);

            if (json.has("id")) {
                System.out.println("\n✅ Job posted successfully!");
                System.out.println("Job ID:  " + json.get("id").asLong());
                System.out.println("Title:   " + json.get("title").asText());
                System.out.println("Budget:  $" + json.get("budget").asDouble());
                System.out.println("Status:  " + json.get("status").asText());
            } else {
                System.out.println("\n❌ Failed:");
                System.out.println(apiClient.prettyPrint(response));
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        pause();
    }

    // ── 6. Submit proposal ────────────────────────────
    static void submitProposal() {
        System.out.println("\n─── SUBMIT PROPOSAL ───");
        if (apiClient.getAuthToken() == null) {
            System.out.println("❌ Please login first.");
            pause();
            return;
        }
        System.out.print("Job ID: ");
        long jobId = Long.parseLong(scanner.nextLine().trim());
        System.out.print("Your Freelancer ID: ");
        long freelancerId = Long.parseLong(scanner.nextLine().trim());
        System.out.print("Cover Letter: ");
        String coverLetter = scanner.nextLine().trim();
        System.out.print("Bid Amount ($): ");
        double bidAmount = Double.parseDouble(scanner.nextLine().trim());

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("jobId", jobId);
            body.put("freelancerId", freelancerId);
            body.put("coverLetter", coverLetter);
            body.put("bidAmount", bidAmount);

            String response = apiClient.postWithAuth("/api/proposals", body);
            JsonNode json = mapper.readTree(response);

            if (json.has("id")) {
                System.out.println("\n✅ Proposal submitted!");
                System.out.println("Proposal ID: " + json.get("id").asLong());
                System.out.println("Job:         " + json.get("jobTitle").asText());
                System.out.println("Bid:         $" + json.get("bidAmount").asDouble());
                System.out.println("Status:      " + json.get("status").asText());
            } else {
                System.out.println("\n❌ Failed:");
                System.out.println(apiClient.prettyPrint(response));
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        pause();
    }

    // ── 7. View proposals by freelancer ───────────────
    static void viewMyProposals() {
        System.out.println("\n─── PROPOSALS BY FREELANCER ───");
        if (apiClient.getAuthToken() == null) {
            System.out.println("❌ Please login first.");
            pause();
            return;
        }
        System.out.print("Enter Freelancer ID: ");
        long freelancerId = Long.parseLong(scanner.nextLine().trim());

        try {
            String response = apiClient.getWithAuth(
                    "/api/proposals/freelancer/" + freelancerId);
            JsonNode json = mapper.readTree(response);

            if (!json.isArray() || json.isEmpty()) {
                System.out.println("No proposals found for this freelancer.");
            } else {
                System.out.println("─────────────────────────────────────────");
                for (JsonNode p : json) {
                    System.out.println("Proposal ID: " + p.get("id").asLong());
                    System.out.println("Job:         " + p.get("jobTitle").asText());
                    System.out.println("Bid:         $" + p.get("bidAmount").asDouble());
                    System.out.println("Status:      " + p.get("status").asText());
                    System.out.println("─────────────────────────────────────────");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        pause();
    }

    // ── 8. Accept proposal ────────────────────────────
    static void acceptProposal() {
        System.out.println("\n─── ACCEPT PROPOSAL ───");
        if (apiClient.getAuthToken() == null) {
            System.out.println("❌ Please login first.");
            pause();
            return;
        }
        System.out.print("Proposal ID to accept: ");
        long proposalId = Long.parseLong(scanner.nextLine().trim());

        try {
            String response = apiClient.putWithAuth(
                    "/api/proposals/" + proposalId + "/accept");
            JsonNode json = mapper.readTree(response);

            if (json.has("id")) {
                System.out.println("\n✅ Proposal accepted!");
                System.out.println("Proposal ID: " + json.get("id").asLong());
                System.out.println("Status:      " + json.get("status").asText());
                System.out.println("Job:         " + json.get("jobTitle").asText());
            } else {
                System.out.println("\n❌ Failed:");
                System.out.println(apiClient.prettyPrint(response));
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        pause();
    }

    // ── 9. Search users ───────────────────────────────
    static void searchUsers() {
        System.out.println("\n─── SEARCH USERS ───");
        if (apiClient.getAuthToken() == null) {
            System.out.println("❌ Please login first.");
            pause();
            return;
        }
        System.out.print("Enter name to search: ");
        String name = scanner.nextLine().trim();

        try {
            String response = apiClient.getWithAuth(
                    "/api/users/search?name=" + name);
            JsonNode json = mapper.readTree(response);

            if (!json.isArray() || json.isEmpty()) {
                System.out.println("No users found with name: " + name);
            } else {
                System.out.println("─────────────────────────────────────────");
                for (JsonNode user : json) {
                    System.out.println("ID:    " + user.get("id").asLong());
                    System.out.println("Name:  " + user.get("name").asText());
                    System.out.println("Email: " + user.get("email").asText());
                    System.out.println("Role:  " + user.get("role").asText());
                    System.out.println("─────────────────────────────────────────");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        pause();
    }

    // ── 10. Filter by budget ──────────────────────────
    static void filterByBudget() {
        System.out.println("\n─── FILTER JOBS BY BUDGET ───");
        if (apiClient.getAuthToken() == null) {
            System.out.println("❌ Please login first.");
            pause();
            return;
        }
        System.out.print("Minimum budget ($): ");
        double min = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Maximum budget ($): ");
        double max = Double.parseDouble(scanner.nextLine().trim());

        try {
            String response = apiClient.getWithAuth(
                    "/api/jobs/budget?min=" + min + "&max=" + max);
            JsonNode json = mapper.readTree(response);

            if (!json.isArray() || json.isEmpty()) {
                System.out.println("No jobs found between $" + min + " and $" + max);
            } else {
                System.out.println("Jobs between $" + min + " and $" + max + ":");
                System.out.println("─────────────────────────────────────────");
                for (JsonNode job : json) {
                    System.out.println("ID:      " + job.get("id").asLong());
                    System.out.println("Title:   " + job.get("title").asText());
                    System.out.println("Budget:  $" + job.get("budget").asDouble());
                    System.out.println("Status:  " + job.get("status").asText());
                    System.out.println("Client:  " + job.get("clientName").asText());
                    System.out.println("─────────────────────────────────────────");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        pause();
    }
}