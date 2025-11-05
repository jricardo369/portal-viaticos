package com.viaticos.adapter.out.file;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource(value = "classpath:utilerias.properties")
public class EventosAdapter {

	@Value("${eventos}")
	private String eventos;

	@Value("${eventos.no}")
	private String eventosNo;

	@Value("${eventos.sinenviopoliza}")
	private String sinenviopoliza;

	@Value("${eventos.dispersionentrega}")
	private String dispersionentrega;

	@Value("${eventos.comprobacion}")
	private String comprobacion;

	public List<String> eventos() {
		List<String> salida = Arrays.asList(eventos.split(","));
		return salida;
	}

	public List<String> eventosNo() {
		List<String> salida = Arrays.asList(eventosNo.split(","));
		return salida;
	}

	public String conversionNoEvento(String noEvento) {
		String salida = "";
		if (noEvento.equals("Sin envió de póliza")) {
			salida = sinenviopoliza;
		} else if (noEvento.equals("Sin dispersión")) {
			salida = dispersionentrega;
		} else if (noEvento.equals("Sin envió comprobación")) {
			salida = comprobacion;
		}
		return salida;
	}

}
