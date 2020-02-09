package com.example.beta;

class User {
    private String name, email, phone, uid, password, id, date, weight, height;
    private Boolean isFemale,isPreg;

    /**
     * @param name
     * @param email
     * @param password
     * @param phone
     * @param isFemale
     * @param isPreg
     * @param id
     * @param date
     * @param weight
     * @param height
     * @param uid
     */
    public User (String name, String email, String password, String phone, String id, String date, String weight, String height, Boolean isFemale, Boolean isPreg, String uid) {
        this.name=name;
        this.email=email;
        this.password=password;
        this.phone=phone;
        this.id=id;
        this.date=date;
        this.height=height;
        this.weight=weight;
        this.uid=uid;
        this.isFemale=isFemale;
        this.isPreg=isPreg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid=uid;
    }
}
