package com.example.cat3.ui.volunteer;

public class VolunteerData {
    private String name;
    private String number;
    private String email;

    public VolunteerData() {
        // Default constructor required for Firebase
    }

    public VolunteerData(String name, String number, String email) {
        this.name = name;
        this.number = number;
        this.email = email;
    }

    // Add getters and setters as needed
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

