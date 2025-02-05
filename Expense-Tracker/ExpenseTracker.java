import java.io.*;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;

public class ExpenseTracker {
   
    // Expense class to store expense details
    static class Expense {
        private double amount;
        private String category;
        private String description;
        private LocalDate date;

        public Expense(double amount, String category, String description, LocalDate date) {
            this.amount = amount;
            this.category = category;
            this.description = description;
            this.date = date;
        }

        public double getAmount() { return amount; }
        public String getCategory() { return category; }
        public String getDescription() { return description; }
        public LocalDate getDate() { return date; }

        @Override
        public String toString() {
            return date + " | " + category + " | " + amount + " | " + description;
        }
    }

    // ExpenseManager class to handle expenses
    static class ExpenseManager {
        private List<Expense> expenses;
        private static final String FILE_NAME = "expenses.txt";

        public ExpenseManager() {
            expenses = new ArrayList<>();
            loadExpenses();
        }

        // Add an expense
        public void addExpense(double amount, String category, String description) {
            Expense expense = new Expense(amount, category, description, LocalDate.now());
            expenses.add(expense);
            saveExpenses();
        }

        // View total expenses for a given period
        public double getTotalExpensesForPeriod(String period) {
            LocalDate today = LocalDate.now();
            return expenses.stream()
                    .filter(exp -> filterByPeriod(exp.getDate(), today, period))
                    .mapToDouble(Expense::getAmount)
                    .sum();
        }

        // Filter expenses based on the period (day, week, month)
        private boolean filterByPeriod(LocalDate expenseDate, LocalDate today, String period) {
            switch (period.toLowerCase()) {
                case "day": return expenseDate.equals(today);
                case "week": return expenseDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) ==
                              today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                case "month": return expenseDate.getMonth() == today.getMonth() && expenseDate.getYear() == today.getYear();
                default: return false;
            }
        }

        // Save expenses to file
        private void saveExpenses() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (Expense expense : expenses) {
                    writer.write(expense.getDate() + "," + expense.getCategory() + "," + expense.getAmount() + "," + expense.getDescription());
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error saving expenses: " + e.getMessage());
            }
        }

        // Load expenses from file
        private void loadExpenses() {
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    expenses.add(new Expense(Double.parseDouble(parts[2]), parts[1], parts[3], LocalDate.parse(parts[0])));
                }
            } catch (IOException e) {
                System.out.println("No previous expense data found.");
            }
        }
    }

    // Main function for user interaction
    public static void main(String[] args) {
        ExpenseManager manager = new ExpenseManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Add Expense\n2. View Daily Total\n3. View Weekly Total\n4. View Monthly Total\n5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter amount: ");
                    double amount = scanner.nextDouble();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter category: ");
                    String category = scanner.nextLine();
                    System.out.print("Enter description: ");
                    String description = scanner.nextLine();
                    manager.addExpense(amount, category, description);
                    System.out.println("Expense added.");
                    break;
                case 2:
                    System.out.println("Total for today: " + manager.getTotalExpensesForPeriod("day"));
                    break;
                case 3:
                    System.out.println("Total for this week: " + manager.getTotalExpensesForPeriod("week"));
                    break;
                case 4:
                    System.out.println("Total for this month: " + manager.getTotalExpensesForPeriod("month"));
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option.");

                    scanner.close();
            }

            

        }

        
    }
} 