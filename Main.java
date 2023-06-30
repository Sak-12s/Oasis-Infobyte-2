import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Model
class User {
    private String userID;
    private String pin;
    private String name;
    private Account account;
    private List<Transaction> transactions;

    public User(String userID, String pin, String name) {
        this.userID = userID;
        this.pin = pin;
        this.name = name;
        this.account = new Account();
        this.transactions = new ArrayList<>();
    }

    public String getUserID() {
        return userID;
    }

    public String getPin() {
        return pin;
    }

    public String getName() {
        return name;
    }

    public Account getAccount() {
        return account;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
}

class Account {
    private double balance;

    public Account() {
        this.balance = 0.0;
    }

    public double getBalance() {
        return balance;
    }

    public boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public void deposit(double amount) {
        balance += amount;
    }
}

class Transaction {
    private String type;
    private double amount;

    public Transaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return type + ": $" + amount;
    }
}

// Controller
class ATMController {
    private List<User> users;
    private User currentUser;

    public ATMController() {
        users = new ArrayList<>();
        // Initialize users in the ATM system (for demonstration purposes)
        users.add(new User("123456", "1234", "John Doe"));
        users.add(new User("654321", "5678", "Jane Smith"));
    }

    public boolean authenticateUser(String userID, String pin) {
        for (User user : users) {
            if (user.getUserID().equals(userID) && user.getPin().equals(pin)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public List<Transaction> getTransactionHistory() {
        return currentUser.getTransactions();
    }

    public boolean performWithdrawal(double amount) {
        if (currentUser.getAccount().withdraw(amount)) {
            currentUser.addTransaction(new Transaction("Withdrawal", -amount));
            return true;
        }
        return false;
    }

    public void performDeposit(double amount) {
        currentUser.getAccount().deposit(amount);
        currentUser.addTransaction(new Transaction("Deposit", amount));
    }

    public boolean performTransfer(String recipientID, double amount) {
        User recipient = getUserByID(recipientID);

        if (recipient != null) {
            if (currentUser.getAccount().withdraw(amount)) {
                recipient.getAccount().deposit(amount);
                currentUser.addTransaction(new Transaction("Transfer to " + recipient.getName(), -amount));
                recipient.addTransaction(new Transaction("Transfer from " + currentUser.getName(), amount));
                return true;
            }
        }
        return false;
    }

    private User getUserByID(String userID) {
        for (User user : users) {
            if (user.getUserID().equals(userID)) {
                return user;
            }
        }
        return null;
    }
}

// View
class ATMView {
    private Scanner scanner;
    private ATMController controller;

    public ATMView(ATMController controller) {
        this.scanner = new Scanner(System.in);
        this.controller = controller;
    }

    public void start() {
        System.out.println("Welcome to the ATM!");

        while (true) {
            System.out.print("Enter User ID: ");
            String userID = scanner.nextLine();
            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine();

            if (controller.authenticateUser(userID, pin)) {
                System.out.println("Authentication successful.");
                showMenu();

                while (true) {
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character

                    switch (choice) {
                        case 1:
                            showTransactionHistory();
                            break;
                        case 2:
                            performWithdrawal();
                            break;
                        case 3:
                            performDeposit();
                            break;
                        case 4:
                            performTransfer();
                            break;
                        case 5:
                            System.out.println("Thank you for using the ATM. Goodbye!");
                            return;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }

                    showMenu();
                }
            } else {
                System.out.println("Authentication failed. Please try again.");
            }
        }
    }

    private void showMenu() {
        System.out.println("\nATM Menu:");
        System.out.println("1. View Transaction History");
        System.out.println("2. Withdraw");
        System.out.println("3. Deposit");
        System.out.println("4. Transfer");
        System.out.println("5. Quit");
        System.out.print("Enter your choice: ");
    }

    private void showTransactionHistory() {
        List<Transaction> transactions = controller.getTransactionHistory();
        System.out.println("\nTransaction History:");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        }
    }

    private void performWithdrawal() {
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume the newline character

        if (controller.performWithdrawal(amount)) {
            System.out.println("Withdrawal successful. Please take your cash.");
        } else {
            System.out.println("Insufficient funds. Withdrawal failed.");
        }
    }

    private void performDeposit() {
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume the newline character

        controller.performDeposit(amount);
        System.out.println("Deposit successful.");
    }

    private void performTransfer() {
        System.out.print("Enter recipient's User ID: ");
        String recipientID = scanner.nextLine();
        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume the newline character

        if (controller.performTransfer(recipientID, amount)) {
            System.out.println("Transfer successful.");
        } else {
            System.out.println("Transfer failed. Please check the recipient's User ID and your account balance.");
        }
    }
}

// Main
public class Main {
    public static void main(String[] args) {
        ATMController controller = new ATMController();
        ATMView view = new ATMView(controller);
        view.start();
    }
}

