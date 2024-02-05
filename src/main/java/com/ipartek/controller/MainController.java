package com.ipartek.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ipartek.model.Producto;
import com.ipartek.model.ProductoParaTicket;
import com.ipartek.repository.ProductoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
	
	@Autowired ProductoRepository producto_repo;
	
	@RequestMapping("/")
	public String cargarInicio(HttpSession session, Model model) {
		// OBTENEMOS LA LISTA DE PRODUCTOS
		Enumeration<String> ids_en_sesion = session.getAttributeNames();
		List<String> lista_ids_en_sesion = Collections.list(ids_en_sesion);
		List<ProductoParaTicket> lista_productos_en_sesion = new ArrayList<ProductoParaTicket>();
		for (String id_en_sesion : lista_ids_en_sesion) {
			ProductoParaTicket info_producto = (ProductoParaTicket) session.getAttribute(id_en_sesion);
			lista_productos_en_sesion.add(info_producto);
			System.out.print("ID: " + id_en_sesion);
			System.out.println(" TODO PRODUCTO: " + info_producto);
		}
		
		double importe_total = 0;
		
		for (ProductoParaTicket producto : lista_productos_en_sesion) {
			importe_total += producto.getPrecio_total();
		}
		
		model.addAttribute("atr_importe_total", importe_total);

		System.out.println("PRODUCTOS AÑADIDOS: " + lista_productos_en_sesion);

		model.addAttribute("atr_lista_productos_en_ticket", lista_productos_en_sesion);

		List<Producto> lista_productos = producto_repo.findAll();
		
		for (Producto producto : lista_productos) {
			String nombre_producto = producto.getNombre();
			producto.setNombre(nombre_producto.toUpperCase());
		}
		
		model.addAttribute("atr_lista_productos", lista_productos);
		
		System.out.println(lista_productos);
		return "tpv";
	}

}
