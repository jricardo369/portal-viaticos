package com.viaticos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.viaticos.application.port.out.TareasProgramadasPort;
import com.viaticos.domain.TareaProgramadaEntity;

@SpringBootApplication
@EnableScheduling
public class PortalViaticosApplication {
	
	@Autowired
	private TareasProgramadasPort tareasProgPort;

	public static void main(String[] args) {
		SpringApplication.run(PortalViaticosApplication.class, args);
	}

	@EnableWebSecurity
	@Configuration
	class WebSecurityConfig extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.cors();
			http.csrf().disable()
					.addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
					.authorizeRequests()
					.antMatchers("/swagger-ui.html**").permitAll()
					.antMatchers("/webjars/**").permitAll()
					.antMatchers("/swagger-resources/**").permitAll()
					.antMatchers("/csrf/**").permitAll()
					.antMatchers("/v2/**").permitAll()
					.antMatchers(HttpMethod.POST, "/autenticaciones").permitAll()
					.and().authorizeRequests()
					.antMatchers(HttpMethod.POST, "/empresa").permitAll()
					.and().authorizeRequests()
					.antMatchers("/usuarios/**").permitAll()
					.antMatchers("/envio-correo/**").permitAll()
					.antMatchers("/jobs/**").permitAll()
					.antMatchers("/reportes-pdf/**").permitAll()
					.antMatchers("/viaticos-usuario/**").permitAll()
					.antMatchers("/configuraciones/ambiente**").permitAll()
					
					.anyRequest().authenticated();
		}
		
	}
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*").allowedMethods("PUT", "DELETE","POST","OPTIONS","GET","PATCH").allowedHeaders("*",
                        "Access-Control-Request-Headers").exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
                .allowCredentials(true);
			}
		};
	}
	
	@Bean
	public String getCronTareaProgramadaArchivoCarga()
	{	
		String salida="";
		TareaProgramadaEntity tp = tareasProgPort.obtenerTareaProgramada(1);
		String dia = tp.getDia();
		dia = dia.toUpperCase();
		dia = diaParaCron(dia);
		String hora = tp.getHora();
		// segundo + minuto + hora + dia del mes + mes + dia de la semna
		salida = " 0 " + hora.substring(3, 5) + " " + hora.substring(0,2) + " * * "+dia;
		System.out.println("cron archivo de carga:"+salida);
	    return salida;
	}
	
	@Bean
	public String getCronTareaProgramadaArchivoComp()
	{	
		String salida="";
		TareaProgramadaEntity tp = tareasProgPort.obtenerTareaProgramada(2);
		String dia = tp.getDia();
		dia = dia.toUpperCase();
		dia = diaParaCron(dia);
		String hora = tp.getHora();
		// segundo + minuto + hora + dia del mes + mes + dia de la semna
		salida = " 0 " + hora.substring(3, 5) + " " + hora.substring(0,2) + " * * "+dia;
		System.out.println("cron archivo de comprobacion:"+salida);
	    return salida;
	}
	
	@Bean
	public String getCronTareaProgramadaArchivoPol()
	{	
		String salida="";
		TareaProgramadaEntity tp = tareasProgPort.obtenerTareaProgramada(3);
		String dia = tp.getDia();
		dia = dia.toUpperCase();
		dia = diaParaCron(dia);
		String hora = tp.getHora();
		// segundo + minuto + hora + dia del mes + mes + dia de la semna
		salida = " 0 " + hora.substring(3, 5) + " " + hora.substring(0,2) + " * * "+dia;
		System.out.println("cron archivo de poliza:"+salida);
	    return salida;
	}
	
	@Bean
	public String getCronTareaProgramadaArchivoPolSys21()
	{	
		String salida="";
		TareaProgramadaEntity tp = tareasProgPort.obtenerTareaProgramada(4);
		String dia = tp.getDia();
		dia = dia.toUpperCase();
		dia = diaParaCron(dia);
		String hora = tp.getHora();
		// segundo + minuto + hora + dia del mes + mes + dia de la semna
		salida = " 0 " + hora.substring(3, 5) + " " + hora.substring(0,2) + " * * "+dia;
		System.out.println("cron archivo de poliza SYS21:"+salida);
	    return salida;
	}
	
	@Bean
	public String getCronTareaProgramadaArchivoPolSAPB1()
	{	
		String salida="";
		TareaProgramadaEntity tp = tareasProgPort.obtenerTareaProgramada(5);
		String dia = tp.getDia();
		dia = dia.toUpperCase();
		dia = diaParaCron(dia);
		String hora = tp.getHora();
		// segundo + minuto + hora + dia del mes + mes + dia de la semna
		salida = " 0 " + hora.substring(3, 5) + " " + hora.substring(0,2) + " * * "+dia;
		System.out.println("cron archivo de poliza SAPB1:"+salida);
	    return salida;
	}
	
	@Bean
	public String getCronTareaProgramadaArchivoPolLimpFueraRango()
	{	
		String salida="";
//		TareaProgramadaEntity tp = tareasProgPort.obtenerTareaProgramada(5);
//		String dia = tp.getDia();
//		dia = dia.toUpperCase();
//		dia = diaParaCron(dia);
//		String hora = tp.getHora();
//		// segundo + minuto + hora + dia del mes + mes + dia de la semna
//		salida = " 0 " + hora.substring(3, 5) + " " + hora.substring(0,2) + " * * "+dia;
		salida = " 0 0 1 * * *";
		System.out.println("cron archivo de poliza LIMPIEZA SOLS FUERA DE RANGO:"+salida);
	    return salida;
	}
	
	private String diaParaCron(String dia) {
		String s = "";
		if(dia.equals("LUNES")) {
			s = "MON";
		}
		if(dia.equals("MARTES")) {
			s = "TUE";
		}
		if(dia.equals("MIERCOLES")) {
			s = "WED";
		}
		if(dia.equals("JUEVES")) {
			s = "THU";
		}
		if(dia.equals("VIERNES")) {
			s = "FRI";
		}
		if(dia.equals("SABADO")) {
			s = "SAT";
		}
		if(dia.equals("DOMINGO")) {
			s = "SUN";
		}
		return s;
	}

}
