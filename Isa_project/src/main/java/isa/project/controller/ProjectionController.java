package isa.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import isa.project.domain.Projection;
import isa.project.service.ProjectionService;



@RestController
@RequestMapping(value = "isa/Projection")
public class ProjectionController {
	@Autowired
	private ProjectionService ProjectionService;
	
	@RequestMapping(value="/getProjection", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Projection>> getProjections() {

		List<Projection> projections = ProjectionService.findAll();
		return new ResponseEntity<List<Projection>>(projections, HttpStatus.OK);
	}
	
	@RequestMapping(value="/registerProjection", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public  ResponseEntity<Projection> registerProjection(@RequestBody Projection projection) {
		System.out.println("usaooooo");
		
		Projection newProjection = ProjectionService.save(projection);
		return new ResponseEntity<>(newProjection, HttpStatus.OK);
	}
	
}
