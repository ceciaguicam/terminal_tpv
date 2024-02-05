package com.ipartek.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ipartek.model.Ticket;
import com.ipartek.model.TicketProducto;
import com.ipartek.repository.ProductoRepository;
import com.ipartek.repository.TicketProductoRepository;
import com.ipartek.repository.TicketRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class MenuController {

	@Autowired ProductoRepository producto_repo;
	
	@Autowired TicketRepository ticket_repo;
	
	@Autowired TicketProductoRepository ticket_producto_repo;
	
	@RequestMapping("/tpv")
	public String menu_ventas(Model model) {
		return "redirect:/";
	}
	
	
	@RequestMapping("/tickets")
	public String menu_tickets(Model model, HttpSession session) {
	    
	    List<Ticket> lista_tickets = ticket_repo.findAll();
	    model.addAttribute("atr_lista_tickets", lista_tickets);
	    
	    //CREAMOS UN OBJETO TIPO TICKET VACÍO
	    Ticket ticket_seleccionado = new Ticket();
	    
	    //COMPROBAMOS SI HAY UN TICKET GUARDADO EN LA SESIÓN
	    if (session.getAttribute("ticket_seleccionado") != null) {
	    	
	    	//Si lo hay lo guardamos en una variable de tipo ticket
	        ticket_seleccionado = (Ticket) session.getAttribute("ticket_seleccionado");
	        System.out.println("TICKET SELECCIONADO DESDE MENU: " + ticket_seleccionado);
	        
	        session.invalidate();
	        
	        //Extraemos el id del ticket
	        int id_ticket = ticket_seleccionado.getId();
	        
	        //Llamamos a la función del repository de TicketProducto usando el id del ticket para obtener los productos del ticket y los guardamos en una lista
	        List<TicketProducto> lista_productos_ticket = ticket_producto_repo.obtenerProductosPorTicketId(id_ticket);
	  		System.out.println("LISTA PRODUCTOS EN TICKET: " + lista_productos_ticket);
	        
	        //Metemos la lista en un atr para usar en vista
	        model.addAttribute("atr_lista_productos_ticket", lista_productos_ticket);
	  		
	        //Creamos variable de importe total
	        double importe_total = 0;
	        
	        //Recorremos la lista de productos 
	        for (TicketProducto producto : lista_productos_ticket) {
	        	
	        	//Se extrae el importe de cada producto...
	        	double importe_producto = producto.getImporte();
	        	
	        	//...y se suma al importe total
				importe_total += importe_producto;
			}
	        
	        //Metemos importe total en un atr para usar en vista
	        model.addAttribute("atr_importe_total", importe_total);
	        
	    } else {
	        System.out.println("NO HAY TICKET SELECCIONADO");
	    }

	  //GUARDAMOS EL TICKET SELECCIONADO EN UN ATR PARA USAR EN VISTA
      model.addAttribute("atr_ticket_seleccionado", ticket_seleccionado);
	    
	    return "tickets";
	}
}
