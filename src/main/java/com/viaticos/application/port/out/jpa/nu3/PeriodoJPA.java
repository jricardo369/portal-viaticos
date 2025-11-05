package com.viaticos.application.port.out.jpa.nu3;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.viaticos.domain.sql.nu3.PeriodoEntity;

@Repository
public interface PeriodoJPA extends JpaRepository<PeriodoEntity, Serializable>{
	
		@Query(value = "  SELECT * FROM [TempusNu3].[dbo].[Periodo] p \r\n"
				+ "  WHERE ?1 BETWEEN FechaInicial AND FechaFinal \r\n"
				+ "  AND ID_TipoNomina = 'U' AND Ejercicio = ?2 AND Numero < 60", nativeQuery = true)
	public PeriodoEntity findByFecha(String fecha,String ejercicio);

}
