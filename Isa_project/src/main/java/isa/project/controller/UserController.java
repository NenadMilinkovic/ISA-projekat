package isa.project.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import isa.project.domain.CinemaTheater;
import isa.project.domain.Role;
import isa.project.domain.Status;
import isa.project.domain.User;
import isa.project.service.CinemaTheaterService;
import isa.project.service.UserService;
import isa.project.utils.PasswordStorage;
import isa.project.utils.PasswordStorage.CannotPerformOperationException;
import isa.project.utils.SendEmail;

@RestController
@RequestMapping(value="/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private HttpSession httpSession;	

	@Autowired
	private CinemaTheaterService cinemaTheaterService;

	
	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> registerUser(@RequestBody User user){
		user.setUserRole(Role.USER);
		user.setStatus(Status.NOT_ACTIVATED);
		user.setSalt(PasswordStorage.createSalt());
		user.setRating(0);
		
		String hashedPassword;
		try {
			hashedPassword = PasswordStorage.createHash(user.getSalt(), user.getPassword());
			user.setPassword(hashedPassword);
		} catch (CannotPerformOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		userService.createNewUser(user);
		SendEmail.sendEmail(user.getEmail());
		
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/registerCinemaTheaterAdmin", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> registerCinemaTheaterAdmin(@RequestBody User user, @RequestParam(value = "id") int id){
		
		
		List<CinemaTheater> cinemas = cinemaTheaterService.findAll();
		for(int i = 0; i < cinemas.size(); i++){
			if(cinemas.get(i).getId() == id){
				user.setCinemaTheater(cinemas.get(i));
				cinemas.get(i).getCinemaTheaterAdmin().add(user);
			//	cinemaTheaterService.delete(cinemas.get(i).getId());
			//	CinemaTheater save = cinemaTheaterService.save(cinemas.get(i));
			}
		}
		user.setStatus(Status.NOT_ACTIVATED);
		user.setSalt(PasswordStorage.createSalt());
		
		String hashedPassword;
		try {
			hashedPassword = PasswordStorage.createHash(user.getSalt(), user.getPassword());
			user.setPassword(hashedPassword);
		} catch (CannotPerformOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		userService.createNewUser(user);
		
		SendEmail.sendEmail(user.getEmail());
		
		return new ResponseEntity<>(HttpStatus.CREATED);
		
	}
	
	@RequestMapping(value = "/confirmRegistration/{email:.+}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> confirmRegistration(@PathVariable String email)
	{	
		Long id = 0L;
		
		for(User user : userService.findAll())
		{
			if(user.getEmail().equals(email))
			{
				id = user.getId();
				user.setStatus(Status.ACTIVATED);
				userService.update(id);
			}
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getAllEmails", method = RequestMethod.GET)
	public ResponseEntity<ArrayList<User>> getAllEmails()
	{
		ArrayList<User> allEmails = new ArrayList<>();
		
		for(User user : userService.findAll())
		{
			allEmails.add(user);
		}
		
//		 System.out.println("user ulogovan " + u.getEmail());
		return new ResponseEntity<ArrayList<User>>(allEmails, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> login(@RequestBody User user)
	{
		List<User> users = userService.findAll();
		User logged = new User();
		
		for(User u : users)
		{
			
			if(user.getEmail().equals(u.getEmail()))
			{
				byte[] salt = u.getSalt();
				String pass = user.getPassword();		// hash vrednost
				String hashed = "";
				
				try {
					hashed = PasswordStorage.createHash(salt, pass);
				} catch (CannotPerformOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(u.getPassword().equals(hashed))
				{
					logged = u;
					httpSession.setAttribute("user", logged);
					break;
				
				}else {
					logged = null;
					return new ResponseEntity<User>(logged, HttpStatus.NOT_FOUND);
				}
			}
		}
		return new ResponseEntity<User>(logged, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getLoggedInUser", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> getLoggedInUser()
	{
		User loggedIn = (User)httpSession.getAttribute("user");
//		if(loggedIn != null)
//			System.out.println("uuuuu " + loggedIn.getSurname());
		if(loggedIn == null)
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		return new ResponseEntity<User>(loggedIn, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/searchForFriends", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<User>> searchForFriends(@RequestParam String name, @RequestParam String surname)
	{
		List<User> allUsers = userService.findAll();
		List<User> friends = new ArrayList<>();
		
/*		for(User u : allUsers)
		{
			System.out.println("za usera " + u.getName());
			List<Friendship> friendship = friendshipService.findBySender(u);
			if(u.getStatus().equals(Status.ACTIVATED) && !friendship.isEmpty())
			{
				
					for(Friendship fr : friendship)
					{	
						System.out.println("fr " + fr.getId());
						if(u.getName().toLowerCase().startsWith(name.toLowerCase()) && u.getSurname().toLowerCase().startsWith(surname.toLowerCase()))
						{	
							UserDTO udto = new UserDTO(fr.getReciever(), fr.getStatus());
							hs.add(udto);
							System.out.println("1 " + udto.getName());
						}else if(u.getName().toLowerCase().startsWith(name.toLowerCase()) && surname.equals(" "))
						{	UserDTO udto = new UserDTO(fr.getReciever(), fr.getStatus());
							hs.add(udto);
							System.out.println("2 " + udto.getName());
						}
						else if(u.getSurname().toLowerCase().startsWith(surname.toLowerCase()) && name.equals(" "))
						{	UserDTO udto = new UserDTO(fr.getReciever(), fr.getStatus());
							hs.add(udto);
							System.out.println("3 " + udto.getName());
						}
						else if(name.equals(" ") && surname.equals(" "))
						{	UserDTO udto = new UserDTO(fr.getReciever(), fr.getStatus());
							System.out.println("dodao " + udto.getName());
							hs.add(udto);
						}
					}
				}else if(u.getStatus().equals(Status.ACTIVATED) && friendship.isEmpty())
				{
					System.out.println("dole za usera " + u.getName());
					if(u.getName().toLowerCase().startsWith(name.toLowerCase()) && u.getSurname().toLowerCase().startsWith(surname.toLowerCase()))
					{
					UserDTO udto = new UserDTO(u, null);
					hs.add(udto);
					System.out.println("4 " + udto.getName());
					}else if(u.getName().toLowerCase().startsWith(name.toLowerCase()) && surname.equals(" "))
					{	UserDTO udto = new UserDTO(u, null);
						hs.add(udto);
						System.out.println("5 " + udto.getName());
					}
					else if(u.getSurname().toLowerCase().startsWith(surname.toLowerCase()) && name.equals(" "))
					{	UserDTO udto = new UserDTO(u, null);
						hs.add(udto);
						System.out.println("6 " + udto.getName());
					}
					else if(name.equals(" ") && surname.equals(" "))
					{	UserDTO udto = new UserDTO(u, null);
						hs.add(udto);
						System.out.println("doel dodao " + udto.getName());
					}
					}
			}*/
		
		for(User u : allUsers)
		{
			if(u.getStatus().equals(Status.ACTIVATED))		// u sistemu se ne vide oni koji nisu potvrdili registraciju		
				if(u.getName().toLowerCase().startsWith(name.toLowerCase()) && u.getSurname().toLowerCase().startsWith(surname.toLowerCase()))
					friends.add(u);
				else if(u.getName().toLowerCase().startsWith(name.toLowerCase()) && surname.equals(" "))
					friends.add(u);
				else if(u.getSurname().toLowerCase().startsWith(surname.toLowerCase()) && name.equals(" "))
					friends.add(u);
				else if(name.equals(" ") && surname.equals(" "))
					friends.add(u);
		}		
		
		return new ResponseEntity<List<User>>(friends, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/updateUser/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User user)
	{
		Long ID = Long.valueOf(id);
		System.out.println(ID + " " + user.getName());
		User updated = userService.findOne(ID);
		System.out.println("ovaj updatuje " + updated.getName());
		
		byte[] salt = updated.getSalt();
		String pass = user.getPassword();		// hash vrednost
		String hashed = "";
		System.out.println("up " + updated.getPassword());
		System.out.println("up " + user.getPassword());
		
		if(!pass.equals(""))
		{	try {
				hashed = PasswordStorage.createHash(salt, pass);
			} catch (CannotPerformOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String hashedPassword = "";
			if(!updated.getPassword().equals(hashed))
			{
				updated.setSalt(PasswordStorage.createSalt());
				try {
					hashedPassword = PasswordStorage.createHash(updated.getSalt(), user.getPassword());
					updated.setPassword(hashedPassword);
				} catch (CannotPerformOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		updated.setName(user.getName());
		updated.setSurname(user.getSurname());
		updated.setCity(user.getCity());
		updated.setEmail(user.getEmail());
		updated.setStatus(user.getStatus());
		updated.setPhone(user.getPhone());
		userService.update(ID);
		httpSession.setAttribute("user", updated);
		
		System.out.println("user " + updated.getName());
		
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ResponseEntity<Void> logout()
	{
//		 request.getSession().invalidate();
		httpSession.invalidate();
		
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
}
