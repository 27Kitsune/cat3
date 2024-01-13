package com.example.cat3.ui.volunteer;

public class VolunteerData {

    private String event1;
    private String location1;
    private String date1;
    private String name1;
    private String number1;
    private String email1;
    private String imageUrl1;
    private String name2;
    private String number2;
    private String email2;
    private String ic;
    private String eventId;

    public VolunteerData() {
        // Default constructor required for Firebase
    }

    public VolunteerData(String event1, String location1, String date1, String name1, String number1, String email1, String imageUrl1, String name2, String number2, String email2, String ic, String eventId) {
        this.event1 = event1;
        this.location1 = location1;
        this.date1 = date1;
        this.name1 = name1;
        this.number1 = number1;
        this.imageUrl1 = imageUrl1;
        this.email1 = email1;
        this.name2 = name2;
        this.number2 = number2;
        this.email2 = email2;
        this.ic = ic;
        this.eventId = eventId;
    }

    // Add getters and setters as needed
    public String getevent1() {
        return event1;
    }
    public void setevent1(String event1) { this.event1 = event1; }
    public String getlocation1() {
        return location1;
    }
    public void setlocation1(String location1) { this.location1= location1; }

    public String getDate1() { return date1; }
    public void setDate1(String date1) { this.date1 = date1; }
    public String getName1() {
        return name1;
    }
    public void setName1(String name1) {
        this.name1 = name1;
    }
    public String getNumber1() {
        return number1;
    }
    public void setNumber1(String number1) {
        this.number1 = number1;
    }
    public String getEmail1() {
        return email1;
    }
    public void setEmail1(String email1) {
        this.email1 = email1;
    }
    public String getImageUrl1() {
        return imageUrl1;
    }
    public void setImageUrl1(String imageUrl1) {
        this.imageUrl1 = imageUrl1;
    }
    public String getName2() {
        return name2;
    }
    public void setName2(String name2) {
        this.name2 = name2;
    }
    public String getNumber2() {
        return number2;
    }
    public void setNumber2(String number2) {
        this.number2 = number2;
    }
    public String getEmail2() {
        return email2;
    }
    public void setEmail2(String email2) { this.email2 = email2; }
    public String getIc() {
        return ic;
    }
    public void setIc(String ic) { this.ic = ic; }
    public String getEventId() {return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
}