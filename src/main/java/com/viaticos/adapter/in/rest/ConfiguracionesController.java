package com.viaticos.adapter.in.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viaticos.application.port.in.ConfiguracionesUseCase;
import com.viaticos.domain.ConfiguracionEntity;

@RequestMapping("/configuraciones")
@RestController
@PropertySource(ignoreResourceNotFound = true, value = "classpath:configuraciones-viaticos.properties")
@PropertySource(ignoreResourceNotFound = true, value = "classpath:configuraciones-fox.properties")
public class ConfiguracionesController{
	
	@Value("${ambiente}")
	private String ambiente;
	
	@Value("${rutaArchivos.qas}")
	private String rutaArchivosQAS;
	
	@Value("${rutaArchivos}")
	private String rutaArchivosPRO;
	
	@Value("${ruta.bdfoxqas}")
	private String bDFoxQAS;
	
	@Value("${ruta.bdfoxpro}")
	private String bDFoxPRO;
	
	@Value("${fox-qas-php}")
	private String ambienteFOXenvioQAS;
	
	@Value("${fox-pro-php}")
	private String ambienteFOXenvioPRO;
	
	@Value("${ruta.servicio.sys21.qas}")
	private String servicioSYS21Qas;
	
	@Value("${ruta.servicio.sys21}")
	private String servicioSYS21;
	
	@Value("${ruta.servicio.sapb1.qas}")
	private String servicioSAPB1Qas;
	
	@Value("${ruta.servicio.sapb1.qas}")
	private String servicioSAPB1;
	
	@Autowired
	private ConfiguracionesUseCase configUseCase;
	
	@GetMapping
	public List<ConfiguracionEntity> obtenerConfiguraciones() {
		return configUseCase.obtenerConfiguraciones();
	}
	
	@GetMapping("{codigo}")
	public ConfiguracionEntity obtenerConfiguracion(@PathVariable int codigo) {
		return configUseCase.obtenerConfiguracion(codigo);
	}
	
	@PostMapping
	public void insertarConfiguracion(@RequestBody ConfiguracionEntity codigo) {
		 configUseCase.insertarConfiguracion(codigo);
	}
	
	@PutMapping
	public void actualizarConfiguracion(@RequestBody ConfiguracionEntity codigo,@RequestParam String usuario) {
		System.out.println(codigo.getId()+codigo.getValor1());
		configUseCase.actualizarConfiguracion(codigo,usuario);
	}
	
	@GetMapping("ambiente")
	public String ambiente() {
		StringBuilder datos = new StringBuilder();
		    datos.append("Ambiente:            "+ambiente);
		if("pro".equals(ambiente)) {
			datos.append("\nRuta archivos:       "+rutaArchivosPRO);
			datos.append("\nBD Fox:              "+bDFoxPRO);
			datos.append("\nAmbiente FOX envio:  "+ambienteFOXenvioPRO);
			datos.append("\nServicio SYS21:      "+servicioSYS21);
			datos.append("\nServicio SAPB1:      "+servicioSAPB1);
		}else {
			datos.append("\nRuta archivos:       "+rutaArchivosQAS);
			datos.append("\nBD Fox:              "+bDFoxQAS);
			datos.append("\nAmbiente FOX envio:  "+ambienteFOXenvioQAS);
			datos.append("\nServicio SYS21:      "+servicioSYS21Qas);
			datos.append("\nServicio SAPB1:      "+servicioSAPB1Qas);
		}
		
		
		return datos.toString();
	}

}
