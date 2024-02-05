package com.ipartek.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipartek.model.Producto;
import com.ipartek.repository.ProductoRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class InicializarDatos {
	
	@Autowired
	private ProductoRepository producto_repo;
	
	@PostConstruct
	@Transactional
	public void cargarDatosBD() {
		
		producto_repo.save(new Producto(1, "Croquetas", 6.00, "croquetas.jpg", 10));
		producto_repo.save(new Producto(2, "Tortilla de patatas", 4.00, "tortilla_patatas.jpg", 10));
		producto_repo.save(new Producto(3, "Cerveza", 1.50, "cerveza.jpg", 21));
		producto_repo.save(new Producto(4, "Patatas bravas", 5.00, "patatas_bravas.jpg", 10));
		producto_repo.save(new Producto(5, "Ensaladilla rusa", 6.00, "ensaladilla_rusa.jpg", 10));
		producto_repo.save(new Producto(6, "Agua",  1.00,"agua.jpg", 10));
		producto_repo.save(new Producto(7, "Cazón",  8.00,"cazon.jpg", 10));
		producto_repo.save(new Producto(8, "Coca Cola",  2.5,"coca_cola.jpg", 10));
	}

}
