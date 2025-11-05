package com.viaticos.adapter.in.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viaticos.application.port.in.TareasProgramadasUseCase;
import com.viaticos.domain.TareaProgramadaEntity;

@RequestMapping("/tareas-programadas")
@RestController
public class TareasProgramadasController {

	@Autowired
	private TareasProgramadasUseCase tareaProgUseCase;

	@GetMapping
	public List<TareaProgramadaEntity> obtenerTareaProgramada() {
		return tareaProgUseCase.obtenerTareasProgramadas();
	}

	@GetMapping("{codigo}")
	public TareaProgramadaEntity obtenerTareasProgramadas(@PathVariable int codigo) {
		return tareaProgUseCase.obtenerTareaProgramada(codigo);
	}

	@PostMapping
	public void insertarTareaProgramada(@RequestBody TareaProgramadaEntity tarea) {
		tareaProgUseCase.insertarTareaProgramada(tarea);
	}

	@PutMapping
	public void actualizarTareaProgramada(@RequestBody TareaProgramadaEntity tarea,@RequestParam String usuario) {
		tareaProgUseCase.actualizarTareaProgramada(tarea,usuario);
	}

}
