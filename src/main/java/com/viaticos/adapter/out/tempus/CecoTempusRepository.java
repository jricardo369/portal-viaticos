package com.viaticos.adapter.out.tempus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


import com.viaticos.application.port.out.CecoPort;
import com.viaticos.application.port.out.jpa.nu3.CentroCostoJPA;
import com.viaticos.domain.sql.nu3.CecoEntity;

@Service
@Repository
public class CecoTempusRepository implements CecoPort {

	Logger log = LoggerFactory.getLogger(CecoTempusRepository.class);

	@Autowired
	JdbcTemplate template;

	@Autowired
	private CentroCostoJPA cecoJpa;

	@Override
	public CecoEntity encontrarTextoDeCeco(String ceco) {
		return cecoJpa.findByCeco(ceco);
	}

}
