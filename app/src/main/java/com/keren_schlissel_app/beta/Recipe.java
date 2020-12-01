package com.keren_schlissel_app.beta;

/**
 * a recipe class. build like the firebase tree - recipes.
 */
public class Recipe {
    private String name;
    private Integer location;

    /**
     * an empty builder. not used, but is required in order to use Firebase
     */
    public Recipe(){}

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
