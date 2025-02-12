package models;

public class User { // creating user class with attributes
    private int userId; // Unique ID for the user
    private String phoneNumber;
    private String password;
    private String name;
    private String email;
    private double balance;

    // creating a constructor to initialize a user object
    public User(int userId, String phoneNumber, String password, String name, String email, double balance) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
        this.email = email;
        this.balance = balance;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public double getBalance() {
        return balance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}