package com.viaticos.adapter.out.tempus;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import com.viaticos.application.port.out.AutenticarUsuarioPort;
import com.viaticos.application.port.out.UsuariosPort;
import com.viaticos.application.port.out.jpa.accesos.SEGGrupoJpa;
import com.viaticos.application.port.out.jpa.accesos.SEGUserJPA;
import com.viaticos.application.port.out.jpa.nu3.EmpleadoJPA;
import com.viaticos.application.port.out.jpa.nu3.OrganizacionesJPA;
import com.viaticos.application.port.out.jpa.nu3.SEG_UsuarioOrganizacionJPA;
import com.viaticos.application.port.out.jpa.nu3.SEG_UsuarioRol;
import com.viaticos.domain.Usuario;
import com.viaticos.domain.sql.accesos.SEG_GruposEntity;
import com.viaticos.domain.sql.accesos.UsuarioEntity;
import com.viaticos.domain.sql.nu3.Departamento;
import com.viaticos.domain.sql.nu3.EmpleadoEntity;
import com.viaticos.domain.sql.nu3.Grupo01Model;
import com.viaticos.domain.sql.nu3.OrgEntity;
import com.viaticos.domain.sql.nu3.OrganizacionesModel;
import com.viaticos.domain.sql.nu3.RolModel;
import com.viaticos.domain.sql.nu3.SEG_UsuarioOrganizacionEntity;
import com.viaticos.domain.sql.nu3.SEG_UsuarioRolEntity;

@Service
@Repository
public class UsuariosTempusRepository implements UsuariosPort, AutenticarUsuarioPort {

	Logger log = LoggerFactory.getLogger(UsuariosTempusRepository.class);

	@Autowired
	JdbcTemplate template;

	// private String sentenciaSql;

	@Autowired
	private EmpleadoJPA empleadoJpa;

	@Autowired
	private SEGUserJPA segUserJpa;

	@Autowired
	private SEG_UsuarioOrganizacionJPA segUserOrgJpa;

	@Autowired
	private SEG_UsuarioRol segUsuarioRol;

	@Autowired
	private SEGGrupoJpa segGrupoJpa;

	@Autowired
	private OrganizacionesJPA orgJpa;

	@Override
	public Usuario encontrarUsuarioPorId(String usuario) {
		// Enconrar usuario en base de datos
		Usuario u = new Usuario();

		u.setUsuario("joser");
		u.setPassword("121");

		return u;
	}

	@Override
	public Usuario obtenerContadorDeProductora(String empresa) {
		// Enconrar usuario en base de datos
		Usuario u = new Usuario();
		List<Usuario> us = new ArrayList<>();

		if (!us.isEmpty()) {
			u = us.get(0);
		}

		return u;
	}

	@Override
	public String SHA256(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(input.getBytes());
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < digest.length; i++)
				builder.append(String.format("%02X", digest[i] & 0x000000FF));
			return builder.toString().toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean autenticarUsuario(String usuario, String password) {
		// Llamar usuario tempus
		if (usuario.equals("joser")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Usuario encontrarUsuarioIdJPA(String usuario) {
		
		System.out.println("ENCONTRAR USUARIO JPA");

		EmpleadoEntity empleadoNu3 = new EmpleadoEntity();
		Usuario usuarioMdl = new Usuario();

		List<RolModel> listRol = new ArrayList<RolModel>();
		RolModel rol = new RolModel();

		try {
			System.out.println("---usuario:"+usuario);
			empleadoNu3 = empleadoJpa.findByUserName(usuario);
		} catch (DataAccessException e) {
			// TODO: handle exception
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas.");
		}

		List<Grupo01Model> listGrupoModel = new ArrayList<Grupo01Model>();
		List<OrgEntity> listOrgEntity = new ArrayList<OrgEntity>();
		List<OrganizacionesModel> listOrg = new ArrayList<OrganizacionesModel>();
		List<Departamento> departamentos = new ArrayList<Departamento>();

		System.out.println("empleadoNu3:"+empleadoNu3);
		if (empleadoNu3 != null) {
			// listGrupo = grupo01.findByIdGrupo01AndActivo(empleadoNu3.getIdGrupo01(),
			// true);
			listOrgEntity = orgJpa.findByIdOrganizacion(empleadoNu3.getIdOrganizacion());

			Grupo01Model grupo01M = new Grupo01Model();
			grupo01M.setId(empleadoNu3.getGrupo01En().getIdGrupo01());
			grupo01M.setNombre(empleadoNu3.getGrupo01En().getDescripcion());
			listGrupoModel.add(grupo01M);

			Departamento dep = new Departamento();
			dep.setId(empleadoNu3.getDepEnt().getIdDepartamento());
			dep.setDescripcion(empleadoNu3.getDepEnt().getDescripcion());
			departamentos.add(dep);

			for (OrgEntity org : listOrgEntity) {
				OrganizacionesModel orgModel = new OrganizacionesModel();
				orgModel.setId(org.getIdOrganizacion());
				orgModel.setNombre(org.getNombre());
				orgModel.setRfc(org.getRfc());
				;
				listOrg.add(orgModel);
			}
			
			System.out.println("SE lleno grupo , or, y dep:");

			usuarioMdl.setUsuario(empleadoNu3.getUserName());
			usuarioMdl.setNombre(empleadoNu3.getNombreCompleto());
			usuarioMdl.setCorreoElectronico(empleadoNu3.getCorreoElectronico());
			usuarioMdl.setRol(listRol);
			if (empleadoNu3.getIdTablaAd04() != null)
				usuarioMdl.setNivel(empleadoNu3.getIdTablaAd04().getId());
			usuarioMdl.setOrganizaciones(listOrg);
			usuarioMdl.setGrupo01(listGrupoModel);
			usuarioMdl.setDepartamentos(departamentos);
			usuarioMdl.setFoto(empleadoNu3.getFoto());
			usuarioMdl.setCuentaContable(empleadoNu3.getCuentaContable());
			usuarioMdl.setCeco(empleadoNu3.getIdCentroCosto().getId());
			usuarioMdl.setCecoDesc(empleadoNu3.getIdCentroCosto().getDescripcion());
			usuarioMdl.setPassword(empleadoNu3.getPassPortal());
			usuarioMdl.setRfc(empleadoNu3.getRfc());
			usuarioMdl.setProyecto(empleadoNu3.getProyecto());

			rol.setId("EMP");
			rol.setDescripcion("Empleado");
			listRol.add(rol);
		}

		return usuarioMdl;
	}

	@Override
	public Usuario encontrarUsuarioTempusAccesos(String usuario) {

		UsuarioEntity userNu3 = new UsuarioEntity();
		Usuario user = new Usuario();

		try {
			System.out.println("--------------------usuario:"+usuario);
			userNu3 = segUserJpa.findByusNCortoAndUsActivo(usuario, true);
		} catch (DataAccessException e) {
			// TODO: handle exception
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas.");
		}

		List<RolModel> roles = new ArrayList<RolModel>();

		System.out.println("--------------------userNu3:"+userNu3);
		if (userNu3 != null) {

			if (userNu3.isUsEsAdmin()) {
				RolModel rol = new RolModel();
				rol.setId("ADM");
				rol.setDescripcion("Administrador");

				roles.add(rol);
			}

			// Hacer busqueda a tabla de Roles y organizaciones
			// organizaciones = TempusNu3.SEG_UsuarioOrganizacion
			// roles = TempusNu3.SEG_UsuarioRol y TempusNu3.Rol

			log.info("id user accesos:" + userNu3.getId());
			List<SEG_UsuarioOrganizacionEntity> listOrg = segUserOrgJpa.findByUdId(userNu3.getId());

			List<OrganizacionesModel> org = new ArrayList<OrganizacionesModel>();

			log.info("organizaiones obtenidas:" + listOrg.size());
			for (SEG_UsuarioOrganizacionEntity userOrg : listOrg) {

				OrganizacionesModel orgM = new OrganizacionesModel();

				orgM.setId(userOrg.getOrg().getIdOrganizacion());
				orgM.setNombre(userOrg.getOrg().getNombre());

				org.add(orgM);

			}

			List<SEG_UsuarioRolEntity> listRole = segUsuarioRol.encuentraListaRol(userNu3.getId());
			List<Integer> grupoIds = new ArrayList<Integer>();

			log.info("roles:" + listRole.size());
			// Crea Id's para grupo
			for (SEG_UsuarioRolEntity role : listRole) {
				grupoIds.add(role.getGrpId());
				System.out.println("gpr:"+role.getGrpId());
			}

			List<SEG_GruposEntity> listGrupos = segGrupoJpa.obtenerListaGrupos(grupoIds);

			log.info("listGrupos:" + listGrupos.size());
			for (SEG_GruposEntity grupo : listGrupos) {
				RolModel r = new RolModel();

				r.setId(String.valueOf(grupo.getGrpId()));
				r.setDescripcion(grupo.getGrpNombre().trim());
				roles.add(r);
				System.out.println("rol:"+r);
			}

			user.setUsuario(userNu3.getUsNCorto());
			user.setNombre(userNu3.getUsNombre());
			user.setOrganizaciones(org);
			// user.setCeco("hola");
			// user.setNivel("1");
			user.setCorreoElectronico(userNu3.getUsEmail());
			user.setRol(roles);
			user.setPassword(userNu3.getUsPw());
		}

		return user;
	}

	@Override
	public List<Usuario> encontrarEmpleados(List<String> empleados) {

		List<EmpleadoEntity> empleadoEntity = new ArrayList<EmpleadoEntity>();
		List<Usuario> usuarios = new ArrayList<Usuario>();

		empleadoEntity = empleadoJpa.obtenerEmpleados(empleados);

		for (EmpleadoEntity emp : empleadoEntity) {
			Usuario u = new Usuario();
			u.setUsuario(emp.getUserName());
			u.setNombre(emp.getNombreCompleto());
			u.setCorreoElectronico(emp.getCorreoElectronico());
			u.setFoto(emp.getFoto());

			List<Departamento> departamentos = new ArrayList<Departamento>();
			Departamento departamento = new Departamento();
			departamento.setId(emp.getDepEnt().getIdDepartamento());
			departamento.setDescripcion(emp.getDepEnt().getDescripcion());
			departamentos.add(departamento);
			u.setDepartamentos(departamentos);

			List<Grupo01Model> grupo01s = new ArrayList<Grupo01Model>();
			Grupo01Model grupo = new Grupo01Model();
			grupo.setId(emp.getGrupo01En().getIdGrupo01());
			grupo.setNombre(emp.getGrupo01En().getDescripcion());
			grupo01s.add(grupo);
			u.setGrupo01(grupo01s);

			usuarios.add(u);
		}

		return usuarios;
	}

	@Override
	public List<UsuarioEntity> encontrarUsuarioTempusPorEmpresa(String empresa, String rol) {

		// Obtener lista de usuarios por empresa;
		List<SEG_UsuarioOrganizacionEntity> empresas = new ArrayList<SEG_UsuarioOrganizacionEntity>();
		List<Integer> usuarios = new ArrayList<Integer>();
		List<SEG_UsuarioRolEntity> usuarioRol = new ArrayList<SEG_UsuarioRolEntity>();
		List<Integer> usuariosTempusId = new ArrayList<Integer>();
		List<UsuarioEntity> segUsuarios = new ArrayList<UsuarioEntity>();
		SEG_GruposEntity segGrupo = new SEG_GruposEntity();

		empresas = segUserOrgJpa.findByIdOrganizacion(empresa);

		for (SEG_UsuarioOrganizacionEntity e : empresas) {
			usuarios.add(e.getUdId());
		}

		segGrupo = segGrupoJpa.findByGrpNombre(rol); // contabilidad

		usuarioRol = segUsuarioRol.encuentraRolesPorUsuarioYID(usuarios, segGrupo.getGrpId());

		for (SEG_UsuarioRolEntity u : usuarioRol) {
			usuariosTempusId.add(u.getUsId());
		}

		segUsuarios = segUserJpa.encuentraUsuariosPorId(usuariosTempusId);

		return segUsuarios;
	}

	@Override
	public String encriptar(String usuario, String password) {

		StringBuilder sb = new StringBuilder(usuario);

		String usuarioInvertido = sb.reverse().toString();
		int valor1 = 0;
		String valor2;
		String valor21;
		byte[] b1, b2;
		int bF;
		byte bb1 = 0, bb2 = 0;
		String passFinal = "", decHex;

		for (int i = 0; i < password.length(); i++) {

			if (usuarioInvertido.length() > 0) {
				valor1 = (1 + ((i - 1) % usuarioInvertido.length()) == usuarioInvertido.length()) ? 0
						: 1 + ((i - 1) % usuarioInvertido.length());

				valor2 = usuarioInvertido.substring(valor1, valor1 + 1);
				b1 = valor2.getBytes();
				for (Byte b : b1) {
					bb1 = b;
				}

				valor21 = password.substring(i, i + 1);
				b2 = valor21.getBytes();
				for (Byte b : b2) {
					bb2 = b;
				}

				bF = bb1 ^ bb2;
			} else {
				valor21 = password.substring(i, i + 1);
				b2 = valor21.getBytes();
				for (Byte b : b2) {
					bb2 = b;
				}
				bF = bb2;
			}

			decHex = Integer.toHexString(bF);
			passFinal = passFinal + StringUtils.leftPad(decHex, 2, "0").toLowerCase();

		}

		return passFinal;
	}

}
