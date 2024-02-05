package com.ipartek.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.ipartek.model.TicketProducto;

@Repository
public interface TicketProductoRepository extends JpaRepository<TicketProducto, Integer>{

	@Query(value = "select * from tickets_lista_productos where ticket_id=:valor", nativeQuery = true)
	List<TicketProducto> obtenerProductosPorTicketId(@Param("valor") int valor);
	
	
	
	
}
