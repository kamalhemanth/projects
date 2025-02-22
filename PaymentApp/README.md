```
PaymentApp
├─ paymentapp
│  ├─ logs
│  │  ├─ paymentapp-2025-02-20-1.log
│  │  └─ paymentapp.log
│  ├─ pom.xml
│  ├─ src
│  │  ├─ main
│  │  │  ├─ java
│  │  │  │  └─ com
│  │  │  │     └─ example
│  │  │  │        ├─ App.java
│  │  │  │        ├─ database
│  │  │  │        │  └─ DatabaseManager.java
│  │  │  │        ├─ exceptions
│  │  │  │        │  ├─ InsufficientBalanceException.java
│  │  │  │        │  ├─ InvalidInputException.java
│  │  │  │        │  ├─ PaymentAppException.java
│  │  │  │        │  ├─ TransactionFailedException.java
│  │  │  │        │  └─ UserNotFoundException.java
│  │  │  │        ├─ models
│  │  │  │        │  ├─ Bank.java
│  │  │  │        │  ├─ Payment.java
│  │  │  │        │  ├─ Transaction.java
│  │  │  │        │  └─ User.java
│  │  │  │        └─ services
│  │  │  │           ├─ PaymentService.java
│  │  │  │           ├─ TransactionService.java
│  │  │  │           ├─ UserService.java
│  │  │  │           └─ WalletService.java
│  │  │  └─ resources
│  │  │     └─ log4j2.xml
│  │  └─ test
│  │     └─ java
│  │        └─ com
│  │           └─ example
│  │              └─ AppTest.java
│  └─ target
│     ├─ classes
│     │  ├─ com
│     │  │  └─ example
│     │  │     ├─ App.class
│     │  │     ├─ database
│     │  │     │  └─ DatabaseManager.class
│     │  │     ├─ exceptions
│     │  │     │  ├─ InsufficientBalanceException.class
│     │  │     │  ├─ InvalidInputException.class
│     │  │     │  ├─ PaymentAppException.class
│     │  │     │  ├─ TransactionFailedException.class
│     │  │     │  └─ UserNotFoundException.class
│     │  │     ├─ models
│     │  │     │  ├─ Bank.class
│     │  │     │  ├─ Payment.class
│     │  │     │  ├─ Transaction.class
│     │  │     │  └─ User.class
│     │  │     └─ services
│     │  │        ├─ PaymentService.class
│     │  │        ├─ TransactionService.class
│     │  │        ├─ UserService.class
│     │  │        └─ WalletService.class
│     │  └─ log4j2.xml
│     ├─ generated-sources
│     │  └─ annotations
│     ├─ generated-test-sources
│     │  └─ test-annotations
│     ├─ maven-status
│     │  └─ maven-compiler-plugin
│     │     └─ compile
│     │        └─ default-compile
│     │           ├─ createdFiles.lst
│     │           └─ inputFiles.lst
│     └─ test-classes
│        └─ com
│           └─ example
│              └─ AppTest.class
└─ README.md

```
