package com.example.beta;

public class Recipe {
    private String name;
    private Integer location;
    public Recipe(){}
    public Recipe(String name, int location){
        this.name=name;
        this.location=location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    public Integer getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }
}
