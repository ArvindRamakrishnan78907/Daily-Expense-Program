// Daily Expense Tracker Program - Updated for Rupees and Monthly Views
// Demonstrates OOP: Encapsulation, Abstraction, Polymorphism.
// Author: [Your Name] - For educational purposes.

import java.util.*;
import java.util.stream.Collectors;

// Abstract class for Expense - Abstraction
abstract class Expense {
    // Encapsulation: Private fields
    private String date;
    private double amount;

    public Expense(String date, double amount) {
        this.date = date;
        this.amount = amount;
    }

    // Getters (Encapsulation)
    public String getDate() { return date; }
    public double getAmount() { return amount; }

    // Abstract methods for polymorphism
    public abstract String getCategory();
    public abstract String getDetails();
}

// Subclass for Food Expense - Polymorphism
class FoodExpense extends Expense {
    private String item;

    public FoodExpense(String date, double amount, String item) {
        super(date, amount);
        this.item = item;
    }

    @Override
    public String getCategory() { return "Food"; }

    @Override
    public String getDetails() { return "Food: " + item + " - ₹" + getAmount(); }
}

// Subclass for Travel Expense - Polymorphism
class TravelExpense extends Expense {
    private String mode;

    public TravelExpense(String date, double amount, String mode) {
        super(date, amount);
        this.mode = mode;
    }

    @Override
    public String getCategory() { return "Travel"; }

    @Override
    public String getDetails() { return "Travel: " + mode + " - ₹" + getAmount(); }
}

// Subclass for Miscellaneous Expense - Polymorphism
class MiscExpense extends Expense {
    private String name;

    public MiscExpense(String date, double amount, String name) {
        super(date, amount);
        this.name = name;
    }

    public String getName() { return name; }

    @Override
    public String getCategory() { return "Misc-" + name; }

    @Override
    public String getDetails() { return name + " - ₹" + getAmount(); }
}

// ExpenseTracker class to manage expenses - Encapsulation
class ExpenseTracker {
    private List<Expense> expenses;
    private Set<String> miscNames;
    private Set<String> months; // For monthly grouping

    public ExpenseTracker() {
        expenses = new ArrayList<>();
        miscNames = new TreeSet<>();
        months = new TreeSet<>();
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        String month = expense.getDate().substring(0, 7); // Extract yyyy-MM
        months.add(month);
        if (expense instanceof MiscExpense) {
            miscNames.add(((MiscExpense) expense).getName());
        }
        System.out.println("Expense added successfully!");
    }

    // Updated view method with monthly filtering and default to latest month
    public void viewExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }

        // Default to latest month
        String selectedMonth = new ArrayList<>(months).get(months.size() - 1);
        String filter = null;
        boolean viewing = true;

        while (viewing) {
            displayMonthlyTable(selectedMonth, filter);
            System.out.println("\nOptions:");
            System.out.println("1. Choose a different month");
            System.out.println("2. Filter by particular thing (e.g., Food, Lunch, Books)");
            System.out.println("3. Back to menu");
            System.out.print("Choose: ");
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 1) {
                System.out.println("Available months: " + months);
                System.out.print("Enter month (yyyy-MM): ");
                selectedMonth = scanner.nextLine();
            } else if (choice == 2) {
                System.out.print("Enter filter (e.g., Food, Books): ");
                filter = scanner.nextLine();
            } else {
                viewing = false;
            }
        }
    }

    private void displayMonthlyTable(String month, String filter) {
        List<Expense> filteredExpenses = expenses.stream()
            .filter(e -> e.getDate().startsWith(month))
            .filter(e -> filter == null || e.getDetails().toLowerCase().contains(filter.toLowerCase()))
            .collect(Collectors.toList());

        if (filteredExpenses.isEmpty()) {
            System.out.println("No expenses for " + month + (filter != null ? " matching " + filter : ""));
            return;
        }

        TreeSet<String> dates = new TreeSet<>();
        TreeSet<String> usedCategories = new TreeSet<>();
        for (Expense exp : filteredExpenses) {
            dates.add(exp.getDate());
            String cat = exp.getCategory();
            if (cat.startsWith("Misc-")) {
                cat = cat.substring(5); // Remove "Misc-"
            }
            usedCategories.add(cat);
        }

        System.out.println("\nExpenses Table for " + month + (filter != null ? " (Filtered: " + filter + ")" : ""));
        System.out.print(String.format("%-15s", "Date"));
        for (String cat : usedCategories) {
            System.out.print(String.format("%-15s", cat));
        }
        System.out.print(String.format("%-15s\n", "Total"));
        System.out.println("-----------------------------------------------------------------------");

        double grandTotal = 0;
        for (String date : dates) {
            System.out.print(String.format("%-15s", date));
            double dateTotal = 0;
            for (String cat : usedCategories) {
                double catSum = filteredExpenses.stream()
                    .filter(e -> e.getDate().equals(date) && (
                        (e.getCategory().startsWith("Misc-") && e.getCategory().substring(5).equals(cat)) ||
                        (!e.getCategory().startsWith("Misc-") && e.getCategory().equals(cat))
                    ))
                    .mapToDouble(Expense::getAmount)
                    .sum();
                System.out.print(String.format("%-15.2f", catSum));
                dateTotal += catSum;
            }
            System.out.print(String.format("%-15.2f\n", dateTotal));
            grandTotal += dateTotal;
        }

        System.out.println("-----------------------------------------------------------------------");
        System.out.print(String.format("%-15s", "Total"));
        for (String cat : usedCategories) {
            double catTotal = filteredExpenses.stream()
                .filter(e -> (
                    (e.getCategory().startsWith("Misc-") && e.getCategory().substring(5).equals(cat)) ||
                    (!e.getCategory().startsWith("Misc-") && e.getCategory().equals(cat))
                ))
                .mapToDouble(Expense::getAmount)
                .sum();
            System.out.print(String.format("%-15.2f", catTotal));
        }
        System.out.print(String.format("%-15.2f\n", grandTotal));
    }

    public double getTotalExpenses() {
        double total = 0;
        for (Expense exp : expenses) {
            total += exp.getAmount();
        }
        return total;
    }

    public Set<String> getMiscNames() { return miscNames; }

    // New method: Delete expenses by month, item, or date
    public void deleteExpensesMenu() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses to delete.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        boolean deleting = true;
        while (deleting) {
            System.out.println("\nDelete Options:");
            System.out.println("1. Delete all expenses for a specific month");
            System.out.println("2. Delete all expenses for a specific item/category");
            System.out.println("3. Delete expense by date and item/category");
            System.out.println("4. Back");
            System.out.print("Choose: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Available months: " + months);
                    System.out.print("Enter month (yyyy-MM): ");
                    String month = scanner.nextLine();
                    int before = expenses.size();
                    expenses.removeIf(e -> e.getDate().startsWith(month));
                    months.remove(month);
                    System.out.println((before - expenses.size()) + " expenses deleted for " + month);
                    break;
                case 2:
                    System.out.print("Enter item/category to delete (e.g., Food, Books): ");
                    String filter = scanner.nextLine();
                    before = expenses.size();
                    expenses.removeIf(e -> e.getCategory().equalsIgnoreCase(filter) ||
                        (e instanceof MiscExpense && ((MiscExpense) e).getName().equalsIgnoreCase(filter)));
                    System.out.println((before - expenses.size()) + " expenses deleted for " + filter);
                    break;
                case 3:
                    System.out.print("Enter date (yyyy-MM-dd): ");
                    String date = scanner.nextLine();
                    System.out.print("Enter item/category: ");
                    String cat = scanner.nextLine();
                    before = expenses.size();
                    expenses.removeIf(e -> e.getDate().equals(date) &&
                        (e.getCategory().equalsIgnoreCase(cat) ||
                        (e instanceof MiscExpense && ((MiscExpense) e).getName().equalsIgnoreCase(cat))));
                    System.out.println((before - expenses.size()) + " expenses deleted for " + date + " and " + cat);
                    break;
                case 4:
                    deleting = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}

// Main class
public class DailyExpenseProgram2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExpenseTracker tracker = new ExpenseTracker();
        System.out.println("Welcome to Daily Expense Tracker!");

        boolean running = true;
        while (running) {
            System.out.println("\nMenu:");
            System.out.println("1. Add Food Expense");
            System.out.println("2. Add Travel Expense");
            System.out.println("3. Add Miscellaneous Expense");
            System.out.println("4. View Expenses Table");
            System.out.println("5. Salary Advice");
            System.out.println("6. Delete Expense(s)");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");
            int choice = getIntInput(scanner);

            switch (choice) {
                case 1: addFoodExpense(scanner, tracker); break;
                case 2: addTravelExpense(scanner, tracker); break;
                case 3: addMiscExpense(scanner, tracker); break;
                case 4: tracker.viewExpenses(); break;
                case 5: provideSalaryAdvice(scanner, tracker); break;
                case 6: tracker.deleteExpensesMenu(); break;
                case 7: running = false; System.out.println("Goodbye!"); break;
                default: System.out.println("Invalid choice.");
            }
        }
        scanner.close();
    }

    private static void addFoodExpense(Scanner scanner, ExpenseTracker tracker) {
        System.out.print("Enter date (e.g., 2025-08-18): ");
        String date = scanner.nextLine();
        double amount = getDoubleInput(scanner, "Enter amount (₹): ");
        System.out.print("Enter food item: ");
        String item = scanner.nextLine();
        tracker.addExpense(new FoodExpense(date, amount, item));
    }

    private static void addTravelExpense(Scanner scanner, ExpenseTracker tracker) {
        System.out.print("Enter date (e.g., 2025-08-18): ");
        String date = scanner.nextLine();
        double amount = getDoubleInput(scanner, "Enter amount (₹): ");
        System.out.print("Enter travel mode (e.g., Bus): ");
        String mode = scanner.nextLine();
        tracker.addExpense(new TravelExpense(date, amount, mode));
    }

    private static void addMiscExpense(Scanner scanner, ExpenseTracker tracker) {
        Set<String> miscNames = tracker.getMiscNames();
        String name;
        if (!miscNames.isEmpty()) {
            System.out.println("Previous miscellaneous types:");
            List<String> nameList = new ArrayList<>(miscNames);
            for (int i = 0; i < nameList.size(); i++) {
                System.out.println((i + 1) + ". " + nameList.get(i));
            }
            System.out.print("Enter number or 'new' followed by name: ");
            String input = scanner.nextLine().trim();
            if (input.toLowerCase().startsWith("new")) {
                name = input.substring(3).trim();
            } else if (input.matches("\\d+")) {
                int index = Integer.parseInt(input) - 1;
                name = (index >= 0 && index < nameList.size()) ? nameList.get(index) : scanner.nextLine();
            } else {
                name = input;
            }
        } else {
            System.out.print("Enter new type name: ");
            name = scanner.nextLine();
        }
        System.out.print("Enter date (e.g., 2025-08-18): ");
        String date = scanner.nextLine();
        double amount = getDoubleInput(scanner, "Enter amount (₹): ");
        tracker.addExpense(new MiscExpense(date, amount, name));
    }

    private static void provideSalaryAdvice(Scanner scanner, ExpenseTracker tracker) {
        double totalExpenses = tracker.getTotalExpenses();
        if (totalExpenses == 0) {
            System.out.println("No expenses to analyze.");
            return;
        }
        double salary = getDoubleInput(scanner, "Enter monthly salary (₹): ");
        double monthlyExpenses = totalExpenses * 30; // Simple projection
        double expenseRatio = (monthlyExpenses / salary) * 100;
        System.out.println("\nSalary Advice:");
        System.out.printf("Projected Monthly Expenses: ₹%.2f\n", monthlyExpenses);
        System.out.printf("This is %.2f%% of your salary.\n", expenseRatio);
        if (expenseRatio > 70) {
            double decreaseBy = monthlyExpenses - (salary * 0.6);
            System.out.printf("Expenses high. Decrease by ₹%.2f/month.\n", decreaseBy);
        } else if (expenseRatio < 40) {
            double increaseBy = (salary * 0.5) - monthlyExpenses;
            System.out.printf("Room to spend. Increase by ₹%.2f/month.\n", increaseBy);
        } else {
            System.out.println("Expenses balanced.");
        }
    }

    private static int getIntInput(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid. Enter number: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    private static double getDoubleInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.print("Invalid. Enter number: ");
            scanner.next();
        }
        double value = scanner.nextDouble();
        scanner.nextLine();
        return value;
    }
}
