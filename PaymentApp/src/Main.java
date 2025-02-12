import database.DatabaseManager;
import services.UserService;
import services.TransactionService;
import services.PaymentService;
import services.WalletService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        UserService userService = new UserService(dbManager);
        TransactionService transactionService = new TransactionService(dbManager);
        PaymentService paymentService = new PaymentService(dbManager);
        WalletService walletService = new WalletService(dbManager);

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
                System.out.println("\n===== Wellcome to Command line payment application =====");
                System.out.println("1. Update Profile");
                System.out.println("2. Check Wallet balance");
                System.out.println("3. Add Money to Wallet");
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
                        // Check Wallet Balance
                        double balance = walletService.getWalletBalance(userService.getLoggedInUserId());
                        System.out.println("Your wallet balance is: " + balance);
                        break;

                    case 3:
                        // Add Money to Wallet
                        System.out.print("Enter amount to add: ");
                        double amount = scanner.nextDouble();
                        scanner.nextLine(); // Consume newline
                        System.out.println("Choose source type:");
                        System.out.println("1. Bank Account");
                        System.out.println("2. Card");
                        System.out.println("3. UPI");
                        System.out.print("Choose an option: ");
                        int sourceChoice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        String sourceType = "";
                        String sourceDetails = "";
                        switch (sourceChoice) {
                            case 1:
                                sourceType = "BANK";
                                System.out.print("Enter bank account number: ");
                                sourceDetails = "Bank Account: " + scanner.nextLine();
                                break;
                            case 2:
                                sourceType = "CARD";
                                System.out.print("Enter card number: ");
                                sourceDetails = "Card: " + scanner.nextLine();
                                break;
                            case 3:
                                sourceType = "UPI";
                                System.out.print("Enter UPI ID: ");
                                sourceDetails = "UPI: " + scanner.nextLine();
                                break;
                            default:
                                System.out.println("Invalid option. Please try again.");
                        }
                        walletService.addMoneyToWallet(userService.getLoggedInUserId(), amount, sourceType,
                                sourceDetails);
                        break;

                    case 4:
                        // Make Payment (with sub-options)
                        System.out.println("\n===== Make Payment =====");
                        System.out.println("1. Wallet-to-Wallet");
                        System.out.println("2. Wallet-to-Bank");
                        System.out.println("3. Card/UPI/Netbanking to Another User");
                        System.out.println("4. Bank-to-Bank");
                        System.out.print("Choose an option: ");
                        int paymentChoice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        switch (paymentChoice) {
                            case 1:
                                // Wallet-to-Wallet
                                System.out.print("Enter recipient's phone number: ");
                                String recipientPhoneNumber = scanner.nextLine();
                                System.out.print("Enter amount: ");
                                double walletAmount = scanner.nextDouble();
                                scanner.nextLine(); // Consume newline
                                transactionService.createWalletToWalletTransaction(userService.getLoggedInUserId(),
                                        recipientPhoneNumber, walletAmount);
                                break;

                            case 2:
                                // Wallet-to-Bank
                                System.out.print("Enter bank account number: ");
                                String bankAccountNumber = scanner.nextLine();
                                System.out.print("Enter IFSC code: ");
                                String bankIfscCode = scanner.nextLine();
                                System.out.print("Enter amount: ");
                                double bankAmount = scanner.nextDouble();
                                scanner.nextLine(); // Consume newline
                                transactionService.createWalletToBankTransaction(userService.getLoggedInUserId(),
                                        bankAccountNumber, bankIfscCode, bankAmount);
                                break;
                            case 3:
                                // Card/UPI/Netbanking to Another User
                                System.out.print("Enter recipient's user ID: ");
                                int recipientUserId = scanner.nextInt();
                                scanner.nextLine(); // Consume newline
                                System.out.print("Enter amount: ");
                                double paymentAmount = scanner.nextDouble();
                                scanner.nextLine(); // Consume newline
                                System.out.println("Choose payment method:");
                                System.out.println("1. Card");
                                System.out.println("2. UPI");
                                System.out.println("3. Netbanking");
                                System.out.print("Choose an option: ");
                                int methodChoice = scanner.nextInt();
                                scanner.nextLine(); // Consume newline
                                String paymentMethod = "";
                                String paymentDetails = "";
                                switch (methodChoice) {
                                    case 1:
                                        paymentMethod = "CARD";
                                        System.out.print("Enter card number: ");
                                        paymentDetails = "Card: " + scanner.nextLine();
                                        break;
                                    case 2:
                                        paymentMethod = "UPI";
                                        System.out.print("Enter UPI ID: ");
                                        paymentDetails = "UPI: " + scanner.nextLine();
                                        break;
                                    case 3:
                                        paymentMethod = "NETBANKING";
                                        System.out.print("Enter bank account number: ");
                                        String netbankingAccountNumber = scanner.nextLine();
                                        System.out.print("Enter IFSC code: ");
                                        String netbankingIfscCode = scanner.nextLine();
                                        paymentDetails = "Netbanking: Account " + netbankingAccountNumber + ", IFSC "
                                                + netbankingIfscCode;
                                        break;
                                    default:
                                        System.out.println("Invalid option. Please try again.");
                                }
                                transactionService.createCardUpiNetbankingToUserTransaction(
                                        userService.getLoggedInUserId(), recipientUserId, paymentAmount, paymentMethod,
                                        paymentDetails);
                                break;

                            case 4:
                                // Bank-to-Bank
                                System.out.print("Enter your bank account number: ");
                                String fromAccountNumber = scanner.nextLine();
                                System.out.print("Enter your IFSC code: ");
                                String fromIfscCode = scanner.nextLine();
                                System.out.print("Enter recipient's bank account number: ");
                                String toAccountNumber = scanner.nextLine();
                                System.out.print("Enter recipient's IFSC code: ");
                                String toIfscCode = scanner.nextLine();
                                System.out.print("Enter amount: ");
                                double bankToBankAmount = scanner.nextDouble();
                                scanner.nextLine(); // Consume newline
                                transactionService.createBankToBankTransaction(userService.getLoggedInUserId(),
                                        fromAccountNumber, fromIfscCode, toAccountNumber, toIfscCode, bankToBankAmount);
                                break;

                            default:
                                System.out.println("Invalid option. Please try again.");
                        }
                        break;

                    // In Main.java

                    case 5:
                        // Refund Transaction
                        System.out.print("Enter transaction ID: ");
                        int refundTransactionId = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        System.out.println("Choose refund destination:");
                        System.out.println("1. Original Source");
                        System.out.println("2. Wallet");
                        System.out.print("Choose an option: ");
                        int refundChoice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        String refundTo = (refundChoice == 1) ? "ORIGINAL_SOURCE" : "WALLET";

                        System.out.print("Enter reason for refund: ");
                        String refundReason = scanner.nextLine();

                        transactionService.refundTransaction(refundTransactionId, refundTo, refundReason);
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