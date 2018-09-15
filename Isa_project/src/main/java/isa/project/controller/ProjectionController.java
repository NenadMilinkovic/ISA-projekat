package isa.project.controller;

import java.util.ArrayList;
import java.util.List;

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
import isa.project.domain.Term;
import isa.project.service.CinemaTheaterService;
import isa.project.service.HallService;
import isa.project.service.ProjectionService;
import isa.project.service.TermService;


@RestController
@RequestMapping(value = "/projection")
public class ProjectionController {
	
	@Autowired
	ProjectionService projectionService;
	
	@Autowired
	CinemaTheaterService cinemaTheaterService;
	
	@Autowired
	TermService termService;
	
	@Autowired
	HallService hallService;
	
	@RequestMapping(value = "/getProjections", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Projection>> getProjections(@RequestParam(value = "id") Long id){
		List<Projection> projections = projectionService.findAll();
		List<Projection> retVal = new ArrayList<>();
		
		for(Projection pr : projections)
		{
			if(pr.getCinemaTheater().getId().equals(id))
				retVal.add(pr);
			
		}
			
		for(Projection p: retVal)
			System.out.println(p.getName());
		
		return new ResponseEntity<>(retVal, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getHall/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Hall>> getHall(@PathVariable Long id){
		List<Hall> halls = hallService.findAll();
		List<Hall> retVal = new ArrayList<>();
		
		for(Hall hall : halls)
		{
			if(hall.getCinemaTheater().getId().equals(id))
				retVal.add(hall);
			
		}
		
		return new ResponseEntity<>(retVal, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getTerm/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Term>> getTerm(@PathVariable Long id){
		
		
		Projection projection = projectionService.findOne(id);
		List<Term> terms = termService.findAll(projection);
		List<Term> retVal = new ArrayList<>();
		
		for(Term term : terms)
		{
			
			if(term.getProjection().getId().equals(id)){
				retVal.add(term);
			}
		}
		
		return new ResponseEntity<>(retVal, HttpStatus.OK);
	}

	@RequestMapping(value = "/getNames", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Projection> getProjectionsName(@RequestParam Long id)
	{
		System.out.println("ppp " + id);
		Projection projection = projectionService.findOne(id);
		System.out.println("ppp " + projection);
		return new ResponseEntity<>(projection, HttpStatus.OK);
	}
	
	@RequestMapping(value ="/registerProjection/{cinema_id}", method = RequestMethod.POST)
	public ResponseEntity registerProjection(@PathVariable  Long cinema_id, @RequestBody Projection projection){
		
		CinemaTheater ct = cinemaTheaterService.findOne(cinema_id);
		projection.setCinemaTheater(ct);
		projectionService.save(projection);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value ="/deleteProjection/{id}", method = RequestMethod.DELETE)
	public ResponseEntity deleteProjection(@PathVariable  Long id){
		
		Projection projection = projectionService.findOne(id);
		List<Term> terms = termService.findAll(projection);
		for(Term term : terms){
			if(term.getProjection().getId().equals(id)){
				termService.delete(term.getId());
			}
		}
		projectionService.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value ="/changeProjection/{cinema_id}", method = RequestMethod.PUT)
	public ResponseEntity changeProjection(@PathVariable  Long cinema_id,@RequestBody Projection projection){
		
		CinemaTheater ct = cinemaTheaterService.findOne(cinema_id);
		projection.setCinemaTheater(ct);
		projectionService.save(projection);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
