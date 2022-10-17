package com.example.locavoreapp;

import java.util.ArrayList;

public class Offreclass implements Comparable<Offreclass>{
    public String titre;
    public String text;
    public String prix;
    public int nbImages;
    public int nbLikes;
    public String latitude;
    public String longitude;
    public ArrayList<String> listeUserId;
    public String userId;
    public String date;

    public Offreclass (){
    }

    public Offreclass (String titre, String text, String prix){
        this.titre = titre;
        this.text = text;
        this.prix = prix;
        this.nbImages = 0;
        this.nbLikes = 0;
        this.latitude = "";
        this.longitude = "";
        this.listeUserId = new ArrayList<>();
        this.userId = "";
        this.date = "";
    }

    @Override
    public int compareTo(Offreclass o) {
        return this.nbLikes > o.nbLikes? -1 : 1;
    }
}
