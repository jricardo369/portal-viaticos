package com.viaticos.adapter.in.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viaticos.application.port.in.SubCuentasUseCase;
import com.viaticos.domain.SubCuentaContableEntity;

@RequestMapping("/subcuentas-contables")
@RestController
public class SubCuentasContablesController {

	@Autowired
	private SubCuentasUseCase subCCUseCase;

	@GetMapping("{id}")
	public SubCuentaContableEntity obtenerSubcuenta(@PathVariable int id) {
		return subCCUseCase.obtenerSubCuentaContable(id);
	}

	@GetMapping
	public List<SubCuentaContableEntity> obtenerSubcuentas(@RequestParam(required = false) String empresa,
			@RequestParam(required = false) String ceco) {
		if (empresa == null)
			empresa = "";

		if ("".equals(empresa)) {

			return subCCUseCase.obtenerSubCuentaContables();

		} else {
			System.out.println("empresa:"+empresa+"-ceco:"+ceco);

			return subCCUseCase.obtenerSubCuentaEmpCeco(empresa.trim(), ceco.trim());

		}
	}

	@PostMapping("insertar")
	public void insertarSubcuenta(@RequestBody SubCuentaContableEntity scc) {
		subCCUseCase.insertarSubCuentaContable(scc);
	}

	@PutMapping
	public void modificarSubcuenta(@RequestBody SubCuentaContableEntity scc,String usuario) {
		String u = "jj";
		subCCUseCase.modificarSubCuentaContable(scc,u);
	}

	@DeleteMapping("{id}")
	public void eliminarSubcuenta(@PathVariable int id,String usuario) {
		subCCUseCase.eliminarSubCuentaContable(id,usuario);
	}

}
