package com.example.beta;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FBref {

    public static FirebaseAuth refAuth = FirebaseAuth.getInstance();

    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static DatabaseReference refUsers= FBDB.getReference("Users");
    public static DatabaseReference refPlaces= FBDB.getReference("Places");
    public static DatabaseReference refMenu= FBDB.getReference("Menu");
    public static DatabaseReference refSentence= FBDB.getReference("sentence");
    public static DatabaseReference refRecipes= FBDB.getReference("recipes");
    public static DatabaseReference reflunch= refRecipes.child("lunch");
    public static DatabaseReference refdinner= refRecipes.child("dinner");

    public static FirebaseStorage FBST = FirebaseStorage.getInstance();
    public static StorageReference refStor=FBST.getReference();
    public static StorageReference refImages=refStor.child("Images");
    public static StorageReference refRecfiles=refStor.child("Recipes/");
    public static StorageReference refSUPfiles=refStor.child("Supplements/");
    public static StorageReference refSUBfiles=refStor.child("Substitutes/");

   // public static StorageReference pathReference = refStor.child(n).child("lunch");
   // public static StorageReference islandRef = mStorageRef.child("images/island.jpg");






}
