import database.DatabaseManager;
import services.UserService;
import services.TransactionService;
import services.PaymentService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        UserService userService = new UserService(dbManager);
        TransactionService transactionService = new TransactionService(dbManager);
        PaymentService paymentService = new PaymentService(dbManager);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            if (userService.getLoggedInUserId() == -1) {
                // User is not logged in
                System.out.println("\n===== Payment App Menu =====");
                System.out.println("1. Register User");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        // Register User
                        System.out.print("Enter phone number: ");
                        String phoneNumber = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String password = scanner.nextLine();
                        userService.registerUser(phoneNumber, password);
                        break;

                    case 2:
                        // Login
                        System.out.print("Enter phone number: ");
                        String loginPhoneNumber = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String loginPassword = scanner.nextLine();
                        userService.loginUser(loginPhoneNumber, loginPassword);
                        break;

                    case 3:
                        // Exit
                        System.out.println("Exiting the application. Goodbye!");
                        dbManager.closeConnection();
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } else {
                // User is logged in
                System.out.println("\n===== Payment App Menu =====");
                System.out.println("1. Update Profile");
                System.out.println("2. Send Money to Another User");
                System.out.println("3. Send Money to Bank Account");
                System.out.println("4. Make Payment");
                System.out.println("5. Refund Transaction");
                System.out.println("6. View Transaction History");
                System.out.println("7. Logout");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        // Update Profile
                        System.out.print("Enter name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter email: ");
                        String email = scanner.nextLine();
                        System.out.print("Enter phone number: ");
                        String updatedPhoneNumber = scanner.nextLine();
                        userService.updateUser(userService.getLoggedInUserId(), name, email, updatedPhoneNumber);
                        break;

                    case 2:
                        // Send Money to Another User
                        System.out.print("Enter recipient's phone number: ");
                        String recipientPhoneNumber = scanner.nextLine();
                        System.out.print("Enter amount: ");
                        double amount = scanner.nextDouble();
                        scanner.nextLine(); // Consume newline
                        transactionService.createTransactionPaytm(userService.getLoggedInUserId(), recipientPhoneNumber,
                                amount);
                        break;

                    case 3:
                        // Send Money to Bank Account
                        System.out.print("Enter account number: ");
                        String accountNumber = scanner.nextLine();
                        System.out.print("Enter IFSC code: ");
                        String ifscCode = scanner.nextLine();
                        System.out.print("Enter amount: ");
                        double bankAmount = scanner.nextDouble();
                        scanner.nextLine(); // Consume newline
                        transactionService.createTransactionBank(userService.getLoggedInUserId(), accountNumber,
                                ifscCode, bankAmount);
                        break;

                    case 4:
                        // Make Payment
                        System.out.print("Enter transaction ID: ");
                        int transactionId = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        System.out.print("Enter payment method (CARD/UPI/NETBANKING): ");
                        String paymentMethod = scanner.nextLine();
                        System.out.print("Enter payment details: ");
                        String paymentDetails = scanner.nextLine();
                        paymentService.makePayment(transactionId, paymentMethod, paymentDetails);
                        break;

                    case 5:
                        // Refund Transaction
                        System.out.print("Enter transaction ID: ");
                        int refundTransactionId = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        paymentService.refundTransaction(refundTransactionId);
                        break;

                    case 6:
                        // View Transaction History
                        transactionService.viewTransactionsHistory(userService.getLoggedInUserId());
                        break;

                    case 7:
                        // Logout
                        userService.logout();
                        break;

                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        }
    }
}