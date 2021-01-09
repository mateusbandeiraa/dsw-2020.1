package br.unirio.dsw.compartilhador.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import br.unirio.dsw.compartilhador.api.model.ItemCompartilhado;

/**
 * Classe que representa um repositório para acesso a itens compartilhados
 * 
 * @author User
 */
@Transactional(readOnly = true)
public interface ItemCompartilhadoRepository extends JpaRepository<ItemCompartilhado, Long>
{
	ItemCompartilhado findByNome(String nome);

	ItemCompartilhado findByNomeAndUsuarioId(String nome, Long id);

	@Query("SELECT ic FROM ItemCompartilhado ic WHERE ic.usuario.id = :ownerId AND ic.removido = 0") 
	List<ItemCompartilhado> findByUsuarioId(@Param("ownerId") Long ownerId);

	@Query("SELECT ic FROM ItemCompartilhado ic WHERE ic.usuario.id = :ownerId AND ic.removido = 0") 
	Page<ItemCompartilhado> findByUsuarioId(@Param("ownerId") Long ownerId, Pageable pageable);
	
	@Query("SELECT ic FROM ItemCompartilhado ic WHERE ic.usuario.id = :ownerId AND ic.removido = 0 AND (ic.nome like %:itemNome% OR ic.descricao like %:itemDescricao%)") 
	List<ItemCompartilhado> findByUsuarioIdContainingNomeAndDescricao(@Param("ownerId") Long ownerId, @Param("itemNome") String itemNome, @Param("itemDescricao") String itemDescricao);

	@Query("SELECT ic FROM ItemCompartilhado ic WHERE ic.usuario.id = :ownerId AND ic.removido = 0 AND (ic.nome like %:itemNome% OR ic.descricao like %:itemDescricao%)") 
	Page<ItemCompartilhado> findByUsuarioIdContainingNomeAndDescricao(@Param("ownerId") Long ownerId, Pageable pageable, @Param("itemNome") String itemNome, @Param("itemDescricao") String itemDescricao);
}