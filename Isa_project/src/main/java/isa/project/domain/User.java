package isa.project.domain;

import java.util.ArrayList;

import javax.persistence.*;

@Entity
@Table(name="Users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Integer id;
	@Column(name="userRole", unique=false, nullable=false)
	private Role userRole;
	@Column(name="name", unique=false, nullable=false)
	private String name;
	@Column(name="surname", unique=false, nullable=false)
	private String surname;
	@Column(name="password", unique=false, nullable=false)
	private String password;
	@Column(name="email", unique=false, nullable=false)
	private String email;
	@Column(name="city", unique=false, nullable=false)
	private String city;
	@Column(name="phone", unique=false, nullable=false)
	private String phone;
	private ArrayList<User> friends = new ArrayList<User>();
	private ArrayList<Props> list_of_props = new ArrayList<Props>();
	
	
	public Role getUserRole() {
		return userRole;
	}
	public void setUserRole(Role userRole) {
		this.userRole = userRole;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public ArrayList<User> getFriends() {
		return friends;
	}
	public void setFriends(ArrayList<User> friends) {
		this.friends = friends;
	}
	public ArrayList<Props> getList_of_props() {
		return list_of_props;
	}
	public void setList_of_props(ArrayList<Props> list_of_props) {
		this.list_of_props = list_of_props;
	}
	
	
}
