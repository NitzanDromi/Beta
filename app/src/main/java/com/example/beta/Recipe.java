package com.example.beta;

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
    /*
     * Recipe class builder.
     * this function gets all of the variables that are required in order to assemble a recipe in the firebase.
     * @param name - the recipe's name
     * @param location - the recipe's location in the firebase storage
     */
/* public Recipe(String name, int location){
        this.name=name;
        this.location=location;
    }*/

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
