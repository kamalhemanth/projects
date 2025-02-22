package com.example;

import java.util.Scanner;

import com.example.database.DatabaseManager;
import com.example.exceptions.*;
import com.example.services.PaymentService;
import com.example.services.TransactionService;
import com.example.services.UserService;
import com.example.services.WalletService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        UserService userService = new UserService(dbManager);
        TransactionService transactionService = new TransactionService(dbManager);
        PaymentService paymentService = new PaymentService(dbManager);
        WalletService walletService = new WalletService(dbManager);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                if (userService.getLoggedInUserId() == -1) {

                    logger.info("\n===== Payment App Menu =====");
                    logger.info("1. Register User");
                    logger.info("2. Login");
                    logger.info("3. Exit");
                    logger.info("Choose an option: ");
                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    switch (choice) {
                        case 1:

                            logger.info("Enter phone number: ");
                            String phoneNumber = scanner.nextLine();
                            logger.info("Enter password: ");
                            String password = scanner.nextLine();
                            userService.registerUser(phoneNumber, password);
                            break;

                        case 2:

                            logger.info("Enter phone number: ");
                            String loginPhoneNumber = scanner.nextLine();
                            logger.info("Enter password: ");
                            String loginPassword = scanner.nextLine();
                            userService.loginUser(loginPhoneNumber, loginPassword);
                            break;

                        case 3:

                            logger.info("Exiting the application. Goodbye!");
                            dbManager.closeConnection();
                            System.exit(0);
                            break;

                        default:
                            logger.error("Invalid option selected. Please choose a valid option (1-3).");
                    }
                } else {

                    logger.info("\n===== Welcome to Command Line Payment Application =====");
                    logger.info("1. Update Profile");
                    logger.info("2. Check Wallet balance");
                    logger.info("3. Add Money to Wallet");
                    logger.info("4. Make Payment");
                    logger.info("5. Refund Transaction");
                    logger.info("6. View Transaction History");
                    logger.info("7. Logout");
                    logger.info("Choose an option: ");
                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    switch (choice) {
                        case 1:

                            logger.info("Enter name: ");
                            String name = scanner.nextLine();
                            logger.info("Enter email: ");
                            String email = scanner.nextLine();
                            logger.info("Enter phone number: ");
                            String updatedPhoneNumber = scanner.nextLine();
                            userService.updateUser(userService.getLoggedInUserId(), name, email, updatedPhoneNumber);
                            break;

                        case 2:

                            double balance = walletService.getWalletBalance(userService.getLoggedInUserId());
                            logger.info("Your wallet balance is: {}", balance);
                            break;

                        case 3:

                            logger.info("Enter amount to add: ");
                            double amount = scanner.nextDouble();
                            scanner.nextLine();
                            logger.info("Choose source type:");
                            logger.info("1. Bank Account");
                            logger.info("2. Card");
                            logger.info("3. UPI");
                            logger.info("Choose an option: ");
                            int sourceChoice = scanner.nextInt();
                            scanner.nextLine();
                            String sourceType = "";
                            String sourceDetails = "";
                            switch (sourceChoice) {
                                case 1:
                                    sourceType = "BANK";
                                    logger.info("Enter bank account number: ");
                                    sourceDetails = "Bank Account: " + scanner.nextLine();
                                    break;
                                case 2:
                                    sourceType = "CARD";
                                    logger.info("Enter card number: ");
                                    sourceDetails = "Card: " + scanner.nextLine();
                                    break;
                                case 3:
                                    sourceType = "UPI";
                                    logger.info("Enter UPI ID: ");
                                    sourceDetails = "UPI: " + scanner.nextLine();
                                    break;
                                default:
                                    logger.error("Invalid source type selected. Please choose a valid option (1-3).");
                            }
                            walletService.addMoneyToWallet(userService.getLoggedInUserId(), amount, sourceType,
                                    sourceDetails);
                            break;

                        case 4:

                            logger.info("\n===== Make Payment =====");
                            logger.info("1. Wallet-to-Wallet");
                            logger.info("2. Wallet-to-Bank");
                            logger.info("3. Card/UPI/Netbanking to Another User");
                            logger.info("4. Bank-to-Bank");
                            logger.info("Choose an option: ");
                            int paymentChoice = scanner.nextInt();
                            scanner.nextLine();

                            switch (paymentChoice) {
                                case 1:

                                    logger.info("Enter recipient's phone number: ");
                                    String recipientPhoneNumber = scanner.nextLine();
                                    logger.info("Enter amount: ");
                                    double walletAmount = scanner.nextDouble();
                                    scanner.nextLine();
                                    transactionService.createWalletToWalletTransaction(userService.getLoggedInUserId(),
                                            recipientPhoneNumber, walletAmount);
                                    break;

                                case 2:

                                    logger.info("Enter bank account number: ");
                                    String bankAccountNumber = scanner.nextLine();
                                    logger.info("Enter IFSC code: ");
                                    String bankIfscCode = scanner.nextLine();
                                    logger.info("Enter amount: ");
                                    double bankAmount = scanner.nextDouble();
                                    scanner.nextLine();
                                    transactionService.createWalletToBankTransaction(userService.getLoggedInUserId(),
                                            bankAccountNumber, bankIfscCode, bankAmount);
                                    break;

                                case 3:

                                    logger.info("Enter recipient's user ID: ");
                                    int recipientUserId = scanner.nextInt();
                                    scanner.nextLine();
                                    logger.info("Enter amount: ");
                                    double paymentAmount = scanner.nextDouble();
                                    scanner.nextLine();
                                    logger.info("Choose payment method:");
                                    logger.info("1. Card");
                                    logger.info("2. UPI");
                                    logger.info("3. Netbanking");
                                    logger.info("Choose an option: ");
                                    int methodChoice = scanner.nextInt();
                                    scanner.nextLine();
                                    String paymentMethod = "";
                                    String paymentDetails = "";
                                    switch (methodChoice) {
                                        case 1:
                                            paymentMethod = "CARD";
                                            logger.info("Enter card number: ");
                                            paymentDetails = "Card: " + scanner.nextLine();
                                            break;
                                        case 2:
                                            paymentMethod = "UPI";
                                            logger.info("Enter UPI ID: ");
                                            paymentDetails = "UPI: " + scanner.nextLine();
                                            break;
                                        case 3:
                                            paymentMethod = "NETBANKING";
                                            logger.info("Enter bank account number: ");
                                            String netbankingAccountNumber = scanner.nextLine();
                                            logger.info("Enter IFSC code: ");
                                            String netbankingIfscCode = scanner.nextLine();
                                            paymentDetails = "Netbanking: Account " + netbankingAccountNumber
                                                    + ", IFSC " + netbankingIfscCode;
                                            break;
                                        default:
                                            logger.error(
                                                    "Invalid payment method selected. Please choose a valid option (1-3).");
                                    }
                                    transactionService.createCardUpiNetbankingToUserTransaction(
                                            userService.getLoggedInUserId(), recipientUserId, paymentAmount,
                                            paymentMethod, paymentDetails);
                                    break;

                                case 4:

                                    logger.info("Enter your bank account number: ");
                                    String fromAccountNumber = scanner.nextLine();
                                    logger.info("Enter your IFSC code: ");
                                    String fromIfscCode = scanner.nextLine();
                                    logger.info("Enter recipient's bank account number: ");
                                    String toAccountNumber = scanner.nextLine();
                                    logger.info("Enter recipient's IFSC code: ");
                                    String toIfscCode = scanner.nextLine();
                                    logger.info("Enter amount: ");
                                    double bankToBankAmount = scanner.nextDouble();
                                    scanner.nextLine();
                                    transactionService.createBankToBankTransaction(userService.getLoggedInUserId(),
                                            fromAccountNumber, fromIfscCode, toAccountNumber, toIfscCode,
                                            bankToBankAmount);
                                    break;

                                default:
                                    logger.error(
                                            "Invalid payment option selected. Please choose a valid option (1-4).");
                            }
                            break;

                        case 5:

                            logger.info("Enter transaction ID: ");
                            int refundTransactionId = scanner.nextInt();
                            scanner.nextLine();

                            logger.info("Choose refund destination:");
                            logger.info("1. Original Source");
                            logger.info("2. Wallet");
                            logger.info("Choose an option: ");
                            int refundChoice = scanner.nextInt();
                            scanner.nextLine();

                            String refundTo = (refundChoice == 1) ? "ORIGINAL_SOURCE" : "WALLET";

                            logger.info("Enter reason for refund: ");
                            String refundReason = scanner.nextLine();

                            transactionService.refundTransaction(refundTransactionId, refundTo, refundReason);
                            break;

                        case 6:

                            transactionService.viewTransactionsHistory(userService.getLoggedInUserId());
                            break;

                        case 7:

                            userService.logout();
                            break;

                        default:
                            logger.error("Invalid option selected. Please choose a valid option (1-7).");
                    }
                }

            } catch (UserNotFoundException e) {
                logger.error("User not found: {}", e.getMessage());
            } catch (InsufficientBalanceException e) {
                logger.error("Insufficient balance: {}", e.getMessage());
            } catch (TransactionFailedException e) {
                logger.error("Transaction failed: {}", e.getMessage());
            } catch (InvalidInputException e) {
                logger.error("Invalid input: {}", e.getMessage());
            } catch (Exception e) {
                logger.error("An unexpected error occurred: {}", e.getMessage(), e);
            }
        }
    }
}