package com.viaticos.adapter.in.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.viaticos.application.port.out.JobFOXPort;
import com.viaticos.application.port.out.JobPort;
import com.viaticos.application.port.out.JobSAPB1Port;
import com.viaticos.application.port.out.JobSYS21Port;

@Component
@PropertySource(value = "classpath:configuraciones-viaticos.properties")
public class LayoutsJobs {

	Logger log = LoggerFactory.getLogger(LayoutsJobs.class);

	@Autowired
	private JobPort jobPort;
	
	@Autowired
	private JobSAPB1Port jobb1Port;
	
	@Autowired
	private JobSYS21Port jobSYS21Port;
	
	@Autowired
	private JobFOXPort jobFOXPort;

	@Scheduled(cron = "#{@getCronTareaProgramadaArchivoCarga}") // Ejemplo "0 12 12 * * WED"
	//@Scheduled(fixedRate = 100000)
	private void generarLayoutCargaDeEntrega() {

		jobPort.generarLayoutCargaEntrega("",true,0);

	}

	@Scheduled(cron = "#{@getCronTareaProgramadaArchivoComp}")
	//@Scheduled(fixedRate = 200000)
	private void generarLayoutCargaDeComprobaciones() {

		jobPort.generarLayoutCargaComprobacion("",true,0);

	}

	@Scheduled(cron = "#{@getCronTareaProgramadaArchivoPol}")
	//@Scheduled(fixedRate = 300000)
	private void enviarDatosPolizaDeComprobacion() {

		jobFOXPort.generarPolizaFox(0,true);

	}
	
	@Scheduled(cron = "#{@getCronTareaProgramadaArchivoPolSys21}")
	//@Scheduled(fixedRate = 300000)
	private void enviarDatosPolizaDeComprobacionSys21() {

		jobSYS21Port.generarPolizaSys21(0, true);

	}
	
	@Scheduled(cron = "#{@getCronTareaProgramadaArchivoPolSAPB1}")
	//@Scheduled(fixedRate = 300000)
	private void enviarDatosPolizaDeComprobacionsapb1() {

		//jobb1Port.generarPolizaSAPB1(0, true);

	}
	
	@Scheduled(cron = "#{@getCronTareaProgramadaArchivoPolLimpFueraRango}")
	//@Scheduled(fixedRate = 300000)
	private void limpiezaSolicitudesFueraRangoEnFecchas() {
		jobPort.limpiarSolicitudesFueraRango(0, true);
	}

}
