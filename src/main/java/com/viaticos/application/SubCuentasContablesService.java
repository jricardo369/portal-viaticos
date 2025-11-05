package com.viaticos.application;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.viaticos.application.port.in.SubCuentasUseCase;
import com.viaticos.application.port.out.EventoConfiguracionPort;
import com.viaticos.application.port.out.SubCuentasContablesPort;
import com.viaticos.domain.EventoConfiguracionEntity;
import com.viaticos.domain.SubCuentaContableEntity;

@Service
public class SubCuentasContablesService implements SubCuentasUseCase {

	@Autowired
	private SubCuentasContablesPort subPort;

	@Autowired
	private EventoConfiguracionPort eventoPort;

	@Override
	public SubCuentaContableEntity obtenerSubCuentaContable(int id) {
		if (id == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es necesario enviar el código a buscar");
		}
		return subPort.obtenerSubcuentaContable(id);
	}

	@Override
	public List<SubCuentaContableEntity> obtenerSubCuentaContables() {
		return subPort.obtenerSubcuentasContables();
	}

	@Override
	public void insertarSubCuentaContable(SubCuentaContableEntity scc) {
		if (scc == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Es necesario enviar los valores de la subcuenta contable a insertar");
		}

		if (scc.getCodigo() == null || "".equals(scc.getCodigo())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Es necesario enviar el código de la subcuenta contable");
		}

		if (scc.getDescripcion() == null || "".equals(scc.getDescripcion())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Es necesario enviar la descripción de la subcuenta contable");
		}
		subPort.insertarSubCuentaContable(scc);
	}

	@Override
	public void modificarSubCuentaContable(SubCuentaContableEntity scc, String usuario) {
		if (scc == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Es necesario enviar los valores de la subcuenta contable a editar");
		}

		if (scc.getCodigo() == null || "".equals(scc.getCodigo())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Es necesario enviar el código de la subcuenta contable");
		}

		if (scc.getDescripcion() == null || "".equals(scc.getDescripcion())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Es necesario enviar la descripción de la subcuenta contable");
		}
		if (scc.getTipo() != null) {
			if (scc.getTipo().equals("")) {
				scc.setTipo(null);
			}
		}
		subPort.modificarSubCuentaContable(scc);
		EventoConfiguracionEntity evento = new EventoConfiguracionEntity();
		evento.setEvento("CAMBIO EN SUB CUENTA CONTABLE");
		evento.setTexto("SE CAMBIO LA SUB CUENTA CONTABLE " + scc.getCodigo());
		evento.setFecha(new Date());
		evento.setUsuario(usuario);
		eventoPort.insertarEvento(evento);

	}

	@Override
	public void eliminarSubCuentaContable(int id, String usuario) {
		if (id == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es necesario enviar el código a eliminar");
		}
		subPort.eliminarSubCuentaContable(id);
		EventoConfiguracionEntity evento = new EventoConfiguracionEntity();
		evento.setEvento("SE ELIMINO SUB CUENTA CONTABLE");
		evento.setTexto("SE ELIMINO EL NIVEL " + id);
		evento.setFecha(new Date());
		evento.setUsuario(usuario);
		eventoPort.insertarEvento(evento);
	}

	@Override
	public List<SubCuentaContableEntity> obtenerSubCuentaEmpCeco(String empresa, String ceco) {
		return subPort.obtenerSubCuentaEmpCeco(empresa, ceco);
	}
}
