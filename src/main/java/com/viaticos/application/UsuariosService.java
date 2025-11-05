package com.viaticos.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.in.UsuariosUseCase;
import com.viaticos.application.port.out.ConfiguracionesPort;
import com.viaticos.application.port.out.UsuariosPort;
import com.viaticos.domain.Usuario;

@Service
public class UsuariosService implements UsuariosUseCase {

	@Autowired
	private UsuariosPort usuariosPort;
	
	@Autowired
	private ConfiguracionesPort confPort;
	
	@Override
	public Usuario buscaInfoUsuario(String usuario) {
		
		Usuario user = new Usuario();
		
		//Busca en tabla Tempus Accesos
		user = usuariosPort.encontrarUsuarioTempusAccesos(usuario);
		
		if(user.getUsuario() == null) {
			//Busca en tabla Tempus Nu3
			user = usuariosPort.encontrarUsuarioIdJPA(usuario);
		}
		
		//Obtener si usuario ve el menu de DIRECTOR
		String usAprobadoresDir = confPort.obtenerConfiguracion(10).getValor1();
		boolean esAprobadorDir = java.util.Arrays.asList(usAprobadoresDir.split(","))
                .contains(usuario);
		System.out.println("esAprobadorDir:"+esAprobadorDir);
		user.setPuedeAprobarSolsDirectores(esAprobadorDir);
		
		return user;
	}

}
