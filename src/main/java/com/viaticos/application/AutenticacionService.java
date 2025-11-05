package com.viaticos.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.viaticos.application.port.in.GenerarTokenDeAutenticacionUseCase;
import com.viaticos.application.port.out.GenerarTokenDeAutenticacionPort;
import com.viaticos.application.port.out.UsuariosPort;
import com.viaticos.domain.Usuario;

@Service
@PropertySource(ignoreResourceNotFound = true, value = "classpath:configuraciones-viaticos.properties")
public class AutenticacionService implements GenerarTokenDeAutenticacionUseCase {
	
	@Value("${ambiente}")
	private String ambiente;
	
	@Value("${validar-enc}")
	private boolean validarEncy;

	@Autowired
	private GenerarTokenDeAutenticacionPort genTokenAutPort;

	@Autowired
	private UsuariosPort usPort;

	@Override
	public String autenticarUsuario(String usuario, String password) {

		Usuario us = new Usuario();

		// Buscar en TempuesAccesos

		us = usPort.encontrarUsuarioTempusAccesos(usuario);
		System.out.println("---------------------------usF:"+us);
		if (us.getUsuario() != null) {
			
			boolean validarEnc = true;
			if(ambiente.equals("qas")) {
				if(validarEncy) {
					validarEnc = true;
				}else {
					validarEnc = false;
				}
			}
			
			if(validarEnc) {
				String passEncrypt = usPort.encriptar(usuario, password);
	
				if (!passEncrypt.equals(us.getPassword())) {
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas.");
				}
			}
			
		} else {

			us = usPort.encontrarUsuarioIdJPA(usuario);
			
			boolean validarEnc = true;
			if(ambiente.equals("qas")) {
				if(validarEncy) {
					validarEnc = true;
				}else {
					validarEnc = false;
				}
			}
			
			if(validarEnc) {
				if (us != null && !password.equals(us.getPassword()))
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas.");
			}
		}

		String token = "";
		token = genTokenAutPort.generarTokenDeAutenticacion(usuario);

		return token;
	}

}
