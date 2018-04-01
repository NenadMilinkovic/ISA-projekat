package isa.project.service;

import java.util.List;

import isa.project.domain.User;
import isa.project.domain.DTO.UserDTO;

public interface UserService {
	
	User findOne(Long id);
	
	List<User> findAll();
	
	User createNewUser(User user);
	
	User delete(Long id);
	
	User update(Long id);

}
