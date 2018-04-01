package isa.project.domain;

import java.util.ArrayList;

public class CinemaTheater {
	private enumProjection type;
	private String name;
	private String adress;
	private String description;
	private float rating;
	//termini pocetka filmova
	private ArrayList<String> time = new ArrayList<String>();
	
	private ArrayList<Card> cards_on_discount = new ArrayList<Card>();
	private ArrayList<Projection> repertoire = new ArrayList<Projection>();
	private ArrayList<Hall> halls = new ArrayList<Hall>();
}
