package com.viaticos.application.port.out.jpa.mysql;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.viaticos.domain.DivisionEntity;

@Repository
public interface DivisionJPA extends JpaRepository<DivisionEntity, Serializable> {
	
	public List<DivisionEntity> findAll();
	public DivisionEntity findByCodigo(String division);
	
	
	@Query(value = "SELECT * FROM division WHERE division in(:division)",nativeQuery = true)
	public DivisionEntity obtenerPorDivision(@Param("division")String division);
	
	
}
