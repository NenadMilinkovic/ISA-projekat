package isa.project.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.DateUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
import isa.project.domain.Hall;
import isa.project.domain.Projection;
import isa.project.domain.Reservation;
import isa.project.domain.ReservationStatus;
import isa.project.domain.ReservationType;
import isa.project.domain.SeatMap;
import isa.project.domain.SeatType;
import isa.project.domain.Term;
import isa.project.domain.User;
import isa.project.domain.enumProjection;
import isa.project.service.CinemaTheaterService;
import isa.project.service.HallService;
import isa.project.service.ProjectionService;
import isa.project.service.ReservationService;
import isa.project.service.TermService;
import isa.project.service.UserService;



@RestController
@RequestMapping(value="/reservation")
public class ReservationController {
	
	@Autowired
	private ReservationService reservationService;
	
	@Autowired 
	private TermService termService;
	
	@Autowired 
	private ProjectionService projectionService;
	
	@Autowired
	private CinemaTheaterService cinemaTheaterService;
	
	@Autowired
	private HttpSession httpSession;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private HallService hallService;
	
	
	@RequestMapping(value="/getCinemas", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<CinemaTheater>> getCinemas() 
	{

		List<CinemaTheater> cinemas = cinemaTheaterService.findByType(enumProjection.valueOf("CINEMA"));
		
		return new ResponseEntity<List<CinemaTheater>>(cinemas, HttpStatus.OK);
	}
	
	@RequestMapping(value="/getCinemas/{cinemaId}/getProjections", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Projection>> getProjectionsForCinema(@PathVariable Long cinemaId) 
	{
		
		List<Projection> projections = new ArrayList<>();
		
		return new ResponseEntity<List<Projection>>(projections, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getCinemaReservations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Reservation>> getCinemaReservation(@RequestParam(value = "id") Long id){
		List<Reservation> retVal = new ArrayList<>();
		
		//List<Hall> halls = hallService.findAll(cinemaTheaterService.findOne(id));
		//List<Term> terms = find
		
		List<Reservation> allreservation = reservationService.findAll();
		for(Reservation reservation : allreservation){
			if(reservation.getTerm().getHall().getCinemaTheater().getId().equals(id)){
				if(reservation.getReservationStatus().equals(ReservationStatus.WATCHED)){
					retVal.add(reservation);
				}
			}
		}
		
		return new ResponseEntity<>(retVal, HttpStatus.OK);
	}
	
	@RequestMapping(value="/getProjection/{projectionId}/getTerms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Term>> getTermsForProjection(@PathVariable Long projectionId) 
	{
		
		Projection projection = projectionService.findOne(projectionId);	
		List<Term> terms = termService.findAll(projection);
		
		return new ResponseEntity<List<Term>>(terms, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/reserve", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> makeReservation(@RequestBody String reservation) throws ClassNotFoundException, IOException, ParseException
	{
		
		ArrayList<Integer> rows = new ArrayList<Integer>();
		ArrayList<Integer> cols = new ArrayList<Integer>();
		JSONParser parser = new JSONParser(); 
		JSONObject json = (JSONObject) parser.parse(reservation);
		
		Projection projection = projectionService.findOne(Long.valueOf((String) json.get("projection")));
		Hall hall = hallService.findOne(Long.valueOf((String) json.get("hall")));
		Term term = termService.findTerm(projection, (String)json.get("date"), (String)json.get("time"), hall);
		
		boolean[][] currFree = SeatMap.convertToSeatMap(term.getSeats()).getFreeSeats();
		term.setSeatMap(SeatMap.convertToSeatMap(term.getSeats()));
		JSONArray jArray = (JSONArray) json.get("seats"); 
		
		if(jArray != null)
		{	for (int i = 0; i < jArray.size(); i++)
			{
				JSONArray jArray2 = (JSONArray) jArray.get(i);
				for(int j = 0; j < jArray2.size(); j++) 
				{
					if((boolean)jArray2.get(j)^(boolean)term.getSeatMap().getFreeSeats()[i][j]) {
						rows.add(i);
						cols.add(j);
					}
				}
			} 
		}else
			return new ResponseEntity<>("\"You have to choose at least one seat.\"", HttpStatus.OK);
		
		if(rows.size() < 0) 
		{
			return new ResponseEntity<String>("\"Someone has already reserved those seats\"", HttpStatus.OK);
		}
		if(checkSeats(rows, cols, term) == false) 
		{
			return new ResponseEntity<String>("\"Someone has already reserved those seats\"", HttpStatus.OK);
		}
		
		if(rows.size() > 5) 
		{
			return new ResponseEntity<String>("\"You can choose a maximum of 5 seats.\"", HttpStatus.OK);
		}
		
		if(reservationService.findReservationByUser((User)httpSession.getAttribute("user"),term) != null) 
		{
			return new ResponseEntity<String>("\"You already have a reservation for this term.\"", HttpStatus.OK);
		}
		
		ArrayList<Reservation> reservations = new ArrayList<Reservation>();
		Reservation res = new Reservation();
		res.setTerm(term);
		res.setReservationStatus(ReservationStatus.CONFIRMED);
		res.setUser((User)httpSession.getAttribute("user"));
		res.setRow(rows.get(0));
		res.setColumn(cols.get(0));
		res.setReservationType(ReservationType.REGULAR);
		
		SeatMap seatTypes = (SeatMap) SeatMap.convertToSeatMap(term.getHall().getSeats());
		SeatType type = seatTypes.getSeatTypes()[rows.get(0)][cols.get(0)];
		int price;
		
		if(type == SeatType.REGULAR) 
		{
			price = term.getPriceRegular();
		}
		else if (type == SeatType.BALCONY) 
		{
			price = term.getPriceBalcony();
		}
		else 
		{
			price = term.getPriceVip();
		}
		
		res.setPrice(price);
		currFree[rows.get(0)][cols.get(0)] = false;
		res.setTimeOfReservation(new Date());
		reservations.add(res);
		
		JSONArray friends = (JSONArray) json.get("friends");
		if(friends.size() != (rows.size()-1)) 
		{
			return new ResponseEntity<String>("\"You have selected an inadequate number of seats or some of the seats got reserved in the mean time\"", HttpStatus.OK);
		}
		
		ArrayList<User> friendsInvited = new ArrayList<User>();
		
		if(friends.size() > 0) 
		{
			for(int i = 1; i < rows.size(); i++) 
			{
				res = new Reservation();
				User friend=userService.findOne((Long)((JSONObject)friends.get(i-1)).get("id"));
				friendsInvited.add(friend);
				
				if(reservationService.findReservationByUser(friend,term) != null) 
				{
					return new ResponseEntity<String>("\"One of your friends already has a reservation for this term.\"", HttpStatus.OK);
				} 
				
				res.setUser(friend);
				seatTypes = (SeatMap) SeatMap.convertToSeatMap(term.getHall().getSeats());
				type = seatTypes.getSeatTypes()[rows.get(i)][cols.get(i)];
				
				if(type == SeatType.REGULAR) 
				{
					price = term.getPriceRegular();
				}
				else if (type == SeatType.BALCONY) 
				{
					price = term.getPriceBalcony();
				}
				else {
					price = term.getPriceVip();
				}
				
				res.setPrice(price);
				res.setReservationStatus(ReservationStatus.WAITING);
				res.setRow(rows.get(i));
				res.setColumn(cols.get(i));
				res.setTerm(term);
				currFree[rows.get(i)][cols.get(i)]=false;
				res.setTimeOfReservation(new Date());
				res.setReservationType(ReservationType.REGULAR);
				reservations.add(res);
			}
		}
		
		if(checkSeats(rows, cols, term) == false) 
		{
			return new ResponseEntity<String>("\"Someone has already reserved those seats\"", HttpStatus.OK);
		}
		
		for(int i = 0; i < reservations.size(); i++) 
		{
			reservationService.createNewReservation(reservations.get(i));
			
		/*	if(reservations.get(i).getReservationStatus().equals(ReservationStatus.WAITING)) 
			{
				SendEmail.confirmReservation(reservations.get(i).getUser().getEmail(), res, (User)httpSession.getAttribute("user"));
			}*/
		}
		
	//	SendEmail.reservationDetails(((User)httpSession.getAttribute("user")).getEmail(), res, friendsInvited);
		SeatMap sMap = new SeatMap();
		sMap.setFreeSeats(currFree);
		termService.update(term.getId(), SeatMap.convertToBytes(sMap));
		
		return new ResponseEntity<>("\"Reservation has been successfuly made.\"", HttpStatus.CREATED);
	}
		
	@RequestMapping(value = "/cancelReservation/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> cancelReservation(@PathVariable Long id) throws ClassNotFoundException, IOException, java.text.ParseException
	{
		
		Reservation reservation = reservationService.findOne(id);
		Date date = new Date();
		String time = reservation.getTerm().getTermDate()+" "+reservation.getTerm().getTermTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		Date inputDate = dateFormat.parse(time);
		
		if (DateUtils.addMinutes(date, 30).after(inputDate)) 
		{
			return new ResponseEntity<>("\"Unable to delete reservation less than 30 minutes before start.\"",HttpStatus.BAD_REQUEST);
		}
		
		SeatMap currSeatMap = (SeatMap) SeatMap.convertToSeatMap(reservation.getTerm().getSeats());
		currSeatMap.getFreeSeats()[reservation.getRow()][reservation.getColumn()]=true;
		termService.update(reservation.getTerm().getId(),SeatMap.convertToBytes(currSeatMap));	
		
		reservationService.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/cancel/{id:.+}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> cancel(@PathVariable String id) throws ClassNotFoundException, IOException
	{	
		Reservation reservation = reservationService.findOne(Long.valueOf(id));
		
		SeatMap currSeatMap = (SeatMap) SeatMap.convertToSeatMap(reservation.getTerm().getSeats());
		currSeatMap.getFreeSeats()[reservation.getRow()][reservation.getColumn()]=true;
		termService.update(reservation.getTerm().getId(),SeatMap.convertToBytes(currSeatMap));	
		
		reservationService.delete(Long.valueOf(id));
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/confirmReservation/{id:.+}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> confirmReservation(@PathVariable String id)
	{	
		reservationService.update(Long.valueOf(id),ReservationStatus.CONFIRMED);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@RequestMapping(value = "/confirmFastReservation", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> confirmFastReservation(@RequestBody String id)
	{	
		User loggedUser = (User) httpSession.getAttribute("user");
		
		Reservation res = reservationService.findOne(Long.parseLong(id));
		res.setReservationType(ReservationType.REGULAR);
		res.setUser(loggedUser);
		reservationService.save(res);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	public boolean checkSeats(ArrayList<Integer> rows, ArrayList<Integer> cols, Term term) throws ClassNotFoundException, IOException {
		
		SeatMap currSeatMap = SeatMap.convertToSeatMap(term.getSeats());
		for(int i = 0; i < rows.size(); i++)
		{
			if(currSeatMap.getFreeSeats()[rows.get(i)][cols.get(i)] == false) 
			{
				return false;
			}
		}
		return true;
	}
	
	@RequestMapping(value="/getReservations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Reservation>> getReservations() 
	{
		
		User loggedUser = (User) httpSession.getAttribute("user");
		
		List<Reservation> reservations = reservationService.findReservationByStatus(loggedUser, ReservationStatus.CONFIRMED);
		ArrayList<Reservation> ret = new ArrayList<Reservation>();
		for(Reservation r : reservations){
			if(r.getReservationType().equals(ReservationType.REGULAR)){
				ret.add(r);
			}
		}
		return new ResponseEntity<List<Reservation>>(ret, HttpStatus.OK);
	}
	
	@RequestMapping(value="/getFastReservation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Reservation>> getFastReservations(@RequestParam(value = "id") Long id) 
	{
		
		//User loggedUser = (User) httpSession.getAttribute("user");
		
		List<Reservation> reservations = reservationService.findAll();
		ArrayList<Reservation> ret = new ArrayList<Reservation>();
		for(Reservation r : reservations){
			if(r.getReservationType().equals(ReservationType.FAST)){
				 if(r.getTerm().getProjection().getCinemaTheater().getId()== id){
					 	ret.add(r);
				 }
			}
		}
		return new ResponseEntity<List<Reservation>>(ret, HttpStatus.OK);
	}


	@RequestMapping(value="/getWatchedReservations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Reservation>> getWatchedReservations() 
	{
		User loggedUser = (User) httpSession.getAttribute("user");
		
		List<Reservation> reservations = reservationService.findReservationByStatus(loggedUser, ReservationStatus.WATCHED);
		
		for(Reservation r : reservations)
			System.out.println(r);
		
		return new ResponseEntity<List<Reservation>>(reservations, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/reserveFast", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> makeFastReservation( @RequestBody String reservation,@RequestParam(value = "discount") String discount) throws ClassNotFoundException, IOException, ParseException
	{
		
		ArrayList<Integer> rows = new ArrayList<Integer>();
		ArrayList<Integer> cols = new ArrayList<Integer>();
		JSONParser parser = new JSONParser(); 
		JSONObject json = (JSONObject) parser.parse(reservation);
		
		Projection projection = projectionService.findOne(Long.valueOf((String) json.get("projection")));
		Hall hall = hallService.findOne(Long.valueOf((String) json.get("hall")));
		Term term = termService.findTerm(projection, (String)json.get("date"), (String)json.get("time"), hall);
		
		boolean[][] currFree = SeatMap.convertToSeatMap(term.getSeats()).getFreeSeats();
		term.setSeatMap(SeatMap.convertToSeatMap(term.getSeats()));
		JSONArray jArray = (JSONArray) json.get("seats"); 
		
		if(jArray != null)
		{	for (int i = 0; i < jArray.size(); i++)
			{
				JSONArray jArray2 = (JSONArray) jArray.get(i);
				for(int j = 0; j < jArray2.size(); j++) 
				{
					if((boolean)jArray2.get(j)^(boolean)term.getSeatMap().getFreeSeats()[i][j]) {
						rows.add(i);
						cols.add(j);
					}
				}
			} 
		}else
			return new ResponseEntity<>("\"You have to choose at least one seat.\"", HttpStatus.OK);
		
		if(rows.size() < 0) 
		{
			return new ResponseEntity<String>("\"Someone has already reserved those seats\"", HttpStatus.OK);
		}
		if(checkSeats(rows, cols, term) == false) 
		{
			return new ResponseEntity<String>("\"Someone has already reserved those seats\"", HttpStatus.OK);
		}
		
		if(rows.size() > 5) 
		{
			return new ResponseEntity<String>("\"You can choose a maximum of 5 seats.\"", HttpStatus.OK);
		}
		
		/*if(reservationService.findReservationByUser((User)httpSession.getAttribute("user"),term) != null) 
		{
			return new ResponseEntity<String>("\"You already have a reservation for this term.\"", HttpStatus.OK);
		}
		*/
		ArrayList<Reservation> reservations = new ArrayList<Reservation>();
		Reservation res = new Reservation();
		res.setTerm(term);
		res.setReservationStatus(ReservationStatus.CONFIRMED);
		res.setUser((User)httpSession.getAttribute("user"));
		res.setRow(rows.get(0));
		res.setColumn(cols.get(0));
		res.setReservationType(ReservationType.FAST);
		
		SeatMap seatTypes = (SeatMap) SeatMap.convertToSeatMap(term.getHall().getSeats());
		SeatType type = seatTypes.getSeatTypes()[rows.get(0)][cols.get(0)];
		int price;
		
		if(type == SeatType.REGULAR) 
		{
			price = term.getPriceRegular();
		}
		else if (type == SeatType.BALCONY) 
		{
			price = term.getPriceBalcony();
		}
		else 
		{
			price = term.getPriceVip();
		}
		
		res.setPrice(price*(100-Integer.parseInt(discount))/100);
		currFree[rows.get(0)][cols.get(0)] = false;
		res.setTimeOfReservation(new Date());
		reservations.add(res);

		if(checkSeats(rows, cols, term) == false) 
		{
			return new ResponseEntity<String>("\"Someone has already reserved those seats\"", HttpStatus.OK);
		}
		
		for(int i = 0; i < reservations.size(); i++) 
		{
			reservationService.createNewReservation(reservations.get(i));
			
		}
		SeatMap sMap = new SeatMap();
		sMap.setFreeSeats(currFree);
		termService.update(term.getId(), SeatMap.convertToBytes(sMap));
		
		return new ResponseEntity<>("\"Reservation has been successfuly made.\"", HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/changeSeats", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> changeSeats(@RequestParam(value = "vip") String vip,@RequestParam(value = "regular") String regular,
			@RequestParam(value = "balcony")String balcony,@RequestParam(value = "hall_id") String hall_id) throws ClassNotFoundException, IOException, ParseException{
		
		Hall hall = hallService.findOne(Long.parseLong(hall_id));
	    boolean [][] allSeats = SeatMap.convertToSeatMap(hall.getSeats()).getFreeSeats();
	    
		projectionService.findById(hall.getCinemaTheater());
		Projection p = new Projection();
		List<Term> terms = termService.findAll(p);
	    for(Term term : terms){
	    	
	        SeatMap currSeatMap = (SeatMap) SeatMap.convertToSeatMap(term.getSeats());
			currSeatMap.getFreeSeats()[2][2]=false;
			termService.update(term.getId(),SeatMap.convertToBytes(currSeatMap));	
	    	
	    }
    
		SeatMap sMap = new SeatMap();
		sMap.setFreeSeats(allSeats);
		hall.setSeatMap(sMap);
		hallService.update(hall.getId());
		
		return new ResponseEntity<String>("",HttpStatus.OK);
	}
	
}
