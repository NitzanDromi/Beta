package com.example.beta;

class User {
    private String name, email, phone, uid, password, id, date, weight, height, places;
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
     * @param places
     * @param uid
     */
    public User (String name, String email, String password, String phone, String id, String date, String weight, String height, Boolean isFemale, Boolean isPreg, String places, String uid) {
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
        this.places=places;
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

    public String getPassword() {return password;}

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsFemale() {return isFemale;}

    public void setIsFemale(boolean isFemale) {
        this.isFemale = isFemale;
    }

    public boolean getIsPreg() {return isPreg;}

    public void setIsPreg(boolean isPreg) {
        this.isPreg = isPreg;
    }

    public String getUid() {return uid;}

    public void setUid(String uid) {this.uid=uid;}

    public String getPlaces() {return places;}

    public void setPlaces(String places) {
        this.places=places;
    }
}
