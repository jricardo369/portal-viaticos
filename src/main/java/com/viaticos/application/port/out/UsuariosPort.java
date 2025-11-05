package com.viaticos.application.port.out;

import java.util.List;

import com.viaticos.domain.Usuario;
import com.viaticos.domain.sql.accesos.UsuarioEntity;

public interface UsuariosPort {

	public Usuario encontrarUsuarioPorId(String usuario);
	public Usuario obtenerContadorDeProductora(String empresa);
	public String SHA256(String input);
	
	public String encriptar(String usuario, String password);
	
	public Usuario encontrarUsuarioIdJPA(String usuario);
	public Usuario encontrarUsuarioTempusAccesos(String usuario);
	public List<Usuario> encontrarEmpleados(List<String> empleados);
	public List<UsuarioEntity> encontrarUsuarioTempusPorEmpresa(String empresa, String rol);
}
