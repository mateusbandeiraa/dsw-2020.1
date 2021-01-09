package br.unirio.dsw.compartilhador.api.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.unirio.dsw.compartilhador.api.model.Compartilhamento;
import br.unirio.dsw.compartilhador.api.model.ItemCompartilhado;
import br.unirio.dsw.compartilhador.api.model.Usuario;
import br.unirio.dsw.compartilhador.api.repository.CompartilhamentoRepository;
import br.unirio.dsw.compartilhador.api.repository.ItemCompartilhadoRepository;
import br.unirio.dsw.compartilhador.api.repository.UsuarioRepository;
import br.unirio.dsw.compartilhador.api.utils.spring.ControllerResponse;
import br.unirio.dsw.compartilhador.api.utils.spring.ResponseData;
import lombok.Data;

@RestController
@RequestMapping("/api/compartilhamento")
@CrossOrigin(origins = "*")
public class CompartilhamentoController {
	private static final Logger log = LoggerFactory.getLogger(CompartilhamentoController.class);

	@Autowired
	private CompartilhamentoRepository compartilhamentoRepository;

	@Autowired
	private ItemCompartilhadoRepository itemRepositorio;

	@Autowired
	private UsuarioRepository usuarioRepositorio;

	@GetMapping("/")
	public ResponseEntity<ResponseData> get(@RequestParam("idItem") long idItem) {
		log.info("Entrando em get com idItem: {}", idItem);
		List<Compartilhamento> findByItemId = compartilhamentoRepository.findByItemId(idItem);
		ArrayList<CompartilhamentoDTO> result = new ArrayList<CompartilhamentoDTO>();
		findByItemId.forEach(compartilhamento -> {
			CompartilhamentoDTO dto = new CompartilhamentoDTO(compartilhamento);
			result.add(dto);
		});
		return ControllerResponse.success(result);
	}

	@PostMapping("{idCompartilhamento}/cancelar")
	public ResponseEntity<ResponseData> cancelar(@PathVariable("idCompartilhamento") long idCompartilhamento) {
		log.info("Entrando em cancelar com idCompartilhamento {}", idCompartilhamento);
		Compartilhamento findById = compartilhamentoRepository.findById(idCompartilhamento).orElse(null);
		if(findById == null) {
			return ResponseEntity.notFound().build();
		}
		findById.setCanceladoDono(true);
		compartilhamentoRepository.save(findById);
		return ControllerResponse.success(new CompartilhamentoDTO(findById));
		
	}

	@PostMapping("")
	public ResponseEntity<ResponseData> novo(@RequestBody NovoCompartilhamentoForm form) {
		log.info("Entrando em novo com parâmetros {}", form);
		Usuario usuario = usuarioRepositorio.findById(form.getIdUsuario()).orElse(null);

		if (usuario == null) {
			return ControllerResponse.fail("usuario", "Usuário não encontrado.");
		}

		ItemCompartilhado item = itemRepositorio.findById(form.getIdItem()).orElse(null);
		if (item == null) {
			return ControllerResponse.fail("item", "Item não encontrado");
		}

		Compartilhamento novoCompartilhamento = new Compartilhamento();

		novoCompartilhamento.setUsuario(usuario);
		novoCompartilhamento.setItem(item);
		novoCompartilhamento.setDataInicio(form.getDataInicioAsLocalDate());
		novoCompartilhamento.setDataTermino(form.getDataTerminoAsLocalDate());

		compartilhamentoRepository.save(novoCompartilhamento);

		return ControllerResponse.success();
	}

}

@Data
class NovoCompartilhamentoForm {
	private long idUsuario;
	private long idItem;
	private String dataInicio;
	private String dataTermino;

	LocalDate getDataInicioAsLocalDate() {
		return LocalDate.parse(dataInicio, DateTimeFormatter.ISO_DATE);
	}

	LocalDate getDataTerminoAsLocalDate() {
		return LocalDate.parse(dataTermino, DateTimeFormatter.ISO_DATE);
	}
}

@Data
class CompartilhamentoDTO {
	private long id;
	private String dataInicio;
	private String dataTermino;
	private String nomeUsuario;
	private String status;

	CompartilhamentoDTO() {

	}

	CompartilhamentoDTO(Compartilhamento compartilhamento) {
		this();
		this.setId(compartilhamento.getId());
		this.setDataInicio(compartilhamento.getDataInicio().format(DateTimeFormatter.ISO_LOCAL_DATE));
		this.setDataTermino(compartilhamento.getDataTermino().format(DateTimeFormatter.ISO_LOCAL_DATE));
		String status = compartilhamento.isAceito() ? "ACEITO"
				: compartilhamento.isRejeitado() ? "REJEITADO"
						: compartilhamento.isCanceladoDono() ? "CANCELADO_DONO"
								: compartilhamento.isCanceladoUsuario() ? "CANCELADO_USUARIO" : "ABERTO";
		this.setStatus(status);
		this.setNomeUsuario(compartilhamento.getUsuario().getNome());
	}
}