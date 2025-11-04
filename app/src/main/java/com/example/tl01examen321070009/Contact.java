package com.example.tl01examen321070009;

public class Contact {

    private int id;
    private String country;
    private String name;
    private String phone;
    private String note;
    private String image;

    public Contact(int id, String country, String name, String phone, String note, String image) {
        this.id = id;
        this.country = country;
        this.name = name;
        this.phone = phone;
        this.note = note;
        this.image = image;
    }

    public Contact(String country, String name, String phone, String note, String image) {
        this.country = country;
        this.name = name;
        this.phone = phone;
        this.note = note;
        this.image = image;
    }

    public int getId() { return id; }
    public String getCountry() { return country; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getNote() { return note; }
    public String getImage() { return image; }

    public void setId(int id) { this.id = id; }
    public void setCountry(String country) { this.country = country; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setNote(String note) { this.note = note; }
    public void setImage(String image) { this.image = image; }
}

