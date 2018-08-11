package isa.project.domain;

import java.util.ArrayList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name="Projection")
public class Projection {
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;

	@Column(name="name", unique=false, nullable=false)
	private String name;
	@Column(name="actors", unique=false, nullable=false)
	private ArrayList<String> actors = new ArrayList<String>();
	@Column(name="genre", unique=false, nullable=false)
	private String genre;
	@Column(name="director", unique=false, nullable=false)
	private String director;
	@Column(name="duration", unique=false, nullable=false)
	private String duration;
	@Column(name="poster", unique=false, nullable=false)
	private String poster;
	@Column(name="rating", unique=false, nullable=false)
	private float rating;
	@Column(name="description", unique=false, nullable=false)
	private String description;
	//u kojim salama se projektuje
/*	@OneToMany(fetch = FetchType.LAZY, mappedBy = "Projection", cascade = CascadeType.ALL)
	@JsonIgnore
	private ArrayList<Hall> halls = new ArrayList<Hall>();
	*/
	@Column(name="price", unique=false, nullable=false)
	private float price;
	
	@Column(name="time", unique=false, nullable=false)
	private ArrayList<Integer> time;

	
	public Projection(String name, ArrayList<String> actors, String genre, String director,
			String duration, String poster, float rating, String description, ArrayList<Hall> halls, float price,
			ArrayList<Integer> time) {
		super();
		this.name = name;
		this.actors = actors;
		this.genre = genre;
		this.director = director;
		this.duration = duration;
		this.poster = poster;
		this.rating = rating;
		this.description = description;
//		this.halls = halls;
		this.price = price;
		this.time = time;
	}
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getActors() {
		return actors;
	}
	public void setActors(ArrayList<String> actors) {
		this.actors = actors;
	}
	
	public ArrayList<Integer> getTime() {
		return time;
	}
	public void setTime(ArrayList<Integer> time) {
		this.time = time;
	}
	
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getPoster() {
		return poster;
	}
	public void setPoster(String poster) {
		this.poster = poster;
	}
	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
/*	public ArrayList<Hall> getHalls() {
		return halls;
	}
	public void setHalls(ArrayList<Hall> halls) {
		this.halls = halls;
	}*/
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	
	
}
