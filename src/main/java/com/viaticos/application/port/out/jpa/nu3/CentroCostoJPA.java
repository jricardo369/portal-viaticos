package com.viaticos.application.port.out.jpa.nu3;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.viaticos.domain.sql.nu3.CecoEntity;

@Repository
public interface CentroCostoJPA extends JpaRepository<CecoEntity, Serializable>{

	@Query(value = "SELECT [Texto] FROM [TempusNu3].[dbo].[CentroCosto] WHERE ID_CentroCosto = ?1", nativeQuery = true)
	public CecoEntity findByCeco(String ceco);
	
}