package isa.project.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import isa.project.domain.Projection;
import isa.project.repositories.ProjectionRepository;
import isa.project.service.ProjectionService;

@Transactional
@Service
public class JpaProjection implements ProjectionService {

	@Autowired
	private ProjectionRepository projectionRepository;
	
	@Override
	public Projection findOne(Long id) {
		// TODO Auto-generated method stub
		return projectionRepository.findOne(id);
	}

	@Override
	public List<Projection> findAll() {
		// TODO Auto-generated method stub
		return projectionRepository.findAll();
	}

	@Override
	public Projection save(Projection projection) {
		// TODO Auto-generated method stub
		return projectionRepository.save(projection);
	}

	@Override
	public List<Projection> save(List<Projection> projections) {
		// TODO Auto-generated method stub
		return projectionRepository.save(projections);
	}

	@Override
	public Projection delete(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(List<Long> ids) {
		// TODO Auto-generated method stub
		for(Long id : ids){
			this.delete(id);
		}
		
	}


}
