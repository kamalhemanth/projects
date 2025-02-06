package models;

public class User {
    private int userId;
    private String phoneNumber;
    private String password;
    private String name;
    private String email;

    public User(int userId, String phoneNumber, String password, String name, String email) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}