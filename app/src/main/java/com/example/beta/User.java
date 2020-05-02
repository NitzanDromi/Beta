package com.example.beta;

/**
 * a User class - contains all of the data required for a user
 */
public class User {
    private String name, lastName, email, phone, uid, id, date, weight, height, places, beforeImage, afterImage;
    private Boolean isFemale;

    /**
     * an empty builder. not used, but is required in order to use Firebase
     */
    public User(){}

    /** User class builder. this function gets all of the variables that are required in order to assemble a user.
     * @param name
     * @param email
     * @param phone
     * @param isFemale
     * @param id
     * @param date
     * @param weight
     * @param height
     * @param places
     * @param beforeImage
     * @param afterImage
     * @param uid
     */
    public User (String name,String lastName, String email, String phone, String id, String date, String weight, String height, Boolean isFemale, String places, String uid,String afterImage, String beforeImage) {
        this.name=name;
        this.lastName=lastName;
        this.email=email;
        this.phone=phone;
        this.id=id;
        this.date=date;
        this.height=height;
        this.weight=weight;
        this.uid=uid;
        this.isFemale=isFemale;
        this.places=places;
        this.afterImage=afterImage;
        this.beforeImage=beforeImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email=email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone=phone;
    }

    public String getId() {return id;}

    public void setId(String id) {this.id = id;  }

    public String getDate() {return date;}

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeight() {return height;}

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {return weight;}

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public boolean getIsFemale() {return isFemale;}

    public void setIsFemale(boolean isFemale) {
        this.isFemale = isFemale;
    }

    public String getUid() {return uid;}

    public void setUid(String uid) {this.uid=uid;}

    public String getPlaces() {return places;}

    public void setPlaces(String places) {
        this.places=places;
    }

    public String getAfterImage() {
        return afterImage;
    }

    public void setAfterImage(String afterImage) {
        this.afterImage = afterImage;
    }

    public void setBeforeImage(String beforeImage) {
        this.beforeImage = beforeImage;
    }

    public String getBeforeImage() {
        return beforeImage;
    }
}
