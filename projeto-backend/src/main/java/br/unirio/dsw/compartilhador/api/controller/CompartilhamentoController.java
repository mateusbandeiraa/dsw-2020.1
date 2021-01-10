package br.unirio.dsw.compartilhador.api.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

	private Usuario usuarioLogado;

	@GetMapping("/")
	public ResponseEntity<ResponseData> get(@RequestParam(name = "idItem", required = false) Long idItem,
			@RequestParam(name = "idUsuario", required = false) Long idUsuario) {
		if (idItem != null) {
			return getByItem(idItem);
		} else if (idUsuario != null) {
			return getByUser(idUsuario);
		} else {
			return ResponseEntity.badRequest().build();
		}
	}

	public ResponseEntity<ResponseData> getByItem(@RequestParam("idItem") long idItem) {
		log.info("Entrando em getByItem com idItem: {}", idItem);
		List<Compartilhamento> findByItemId = compartilhamentoRepository.findByItemId(idItem);
		ArrayList<CompartilhamentoDTO> result = new ArrayList<CompartilhamentoDTO>();
		findByItemId.forEach(compartilhamento -> {
			CompartilhamentoDTO dto = new CompartilhamentoDTO(compartilhamento);
			result.add(dto);
		});
		return ControllerResponse.success(result);
	}

	public ResponseEntity<ResponseData> getByUser(@RequestParam("idUsuario") long idUsuario) {
		log.info("Entrando em getByUser com idItem: {}", idUsuario);
		List<Compartilhamento> findByItemId = compartilhamentoRepository.findByUsuarioId(idUsuario);
		ArrayList<CompartilhamentoDTO> result = new ArrayList<CompartilhamentoDTO>();
		findByItemId.forEach(compartilhamento -> {
			CompartilhamentoDTO dto = new CompartilhamentoDTO(compartilhamento);
			result.add(dto);
		});
		return ControllerResponse.success(result);
	}

	@PostMapping("{idCompartilhamento}/status")
	public ResponseEntity<ResponseData> mudarStatus(@PathVariable("idCompartilhamento") long idCompartilhamento,
			@RequestParam("status") String status) {
		log.info("Entrando em cancelar com idCompartilhamento {}", idCompartilhamento);
		Compartilhamento findById = compartilhamentoRepository.findById(idCompartilhamento).orElse(null);
		if (findById == null) {
			return ResponseEntity.notFound().build();
		}
		switch (status) {
		case "CANCELADO_DONO":
			/* Verifica se o usuario logado é o dono do item */
			if (!findById.getItem().getUsuario().getEmail().equals(obterUsuarioLogado().getEmail())) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
			break;
		case "ACEITO":
		case "REJEITADO":
		case "CANCELADO_USUARIO":
			/* Verifica se o usuario logado é o destinatário do compartilhamento */
			if (!findById.getUsuario().getEmail().equals(obterUsuarioLogado().getEmail())) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
			break;
		default:
			return ControllerResponse.fail("Status inválido.");
		}
		findById.setCanceladoDono(status.equals("CANCELADO_DONO"));
		findById.setAceito(status.equals("ACEITO"));
		findById.setRejeitado(status.equals("REJEITADO"));
		findById.setCanceladoUsuario(status.equals("CANCELADO_USUARIO"));
		compartilhamentoRepository.save(findById);
		return ControllerResponse.success(new CompartilhamentoDTO(findById));

	}

	@PostMapping("")
	public ResponseEntity<ResponseData> novo(@RequestBody NovoCompartilhamentoForm form) {
		log.info("Entrando em novo com parâmetros {}", form);
		Usuario usuario = usuarioRepositorio.findByEmail(form.getEmailUsuario());

		if (usuario == null) {
			return ControllerResponse.fail("usuario", "Usuário não encontrado.");
		}

		if (usuario.getEmail().equals(obterUsuarioLogado().getEmail())) {
			return ControllerResponse.fail("usuario", "Não é possível compartilhar um item consigo mesmo.");
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

	private Usuario obterUsuarioLogado() {
		if (usuarioLogado == null) {
			usuarioLogado = usuarioRepositorio
					.findByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		}
		return usuarioLogado;
	}

}

@Data
class NovoCompartilhamentoForm {
	private String emailUsuario;
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
	private String nomeDono;
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
		this.setNomeDono(compartilhamento.getItem().getUsuario().getNome());
	}
}