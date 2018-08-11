package isa.project.service;

import java.util.List;

import isa.project.domain.Projection;


public interface ProjectionService {
	
	Projection findOne(Long id);
	
	List<Projection> findAll();
	
	Projection save(Projection projection);
	
	List<Projection> save(List<Projection> projections);
	
	Projection delete(Long id);

	void delete(List<Long> ids);
	
}
