package isa.project.service;

import java.util.List;

import isa.project.domain.CinemaTheater;
import isa.project.domain.Hall;
import isa.project.domain.User;

public interface HallService {
	Hall findOne(Long id);
	
	List<Hall> findAll();
	List<Hall> findAll(CinemaTheater ct);
	
	Hall createNewHall(Hall hall);
	
	Hall delete(Long id);
	
	Hall update(Long id);
	
	Hall login(User user);
	
}
