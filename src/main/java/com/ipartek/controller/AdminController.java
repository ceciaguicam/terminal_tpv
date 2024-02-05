package com.ipartek.controller;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ipartek.messages.GestorMensajes;
import com.ipartek.model.Producto;
import com.ipartek.model.ProductoParaTicket;
import com.ipartek.model.Ticket;
import com.ipartek.model.TicketProducto;
import com.ipartek.repository.ProductoRepository;
import com.ipartek.repository.TicketProductoRepository;
import com.ipartek.repository.TicketRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
	
	@Autowired ProductoRepository producto_repo;
	
	@Autowired
	private TicketRepository ticket_repo;
	
	@Autowired
	private TicketProductoRepository ticket_producto_repo;
	
	@RequestMapping("/productos")
	public String menu_productos(Model model) {
		
		List<Producto> lista_productos = producto_repo.findAll();

		for (Producto producto : lista_productos) {
			String nombre_producto = producto.getNombre();
			producto.setNombre(nombre_producto.toUpperCase());
		}

		model.addAttribute("atr_lista_productos", lista_productos);
		
		model.addAttribute("objeto_entidad", new Producto());
		
		return "productos";
	}
	
	
	@PostMapping("/añadirProducto")
	public String guardarProducto(@ModelAttribute @Validated Producto objeto_entidad, BindingResult result,
			@RequestParam("foto") MultipartFile file, Model model) {

		try {
			String ruta_foto = "C:\\Users\\Ceci\\Documents\\workspace-spring-tool-suite-4-4.21.0.RELEASE\\terminal_tpv\\src\\main\\resources\\static\\images";
			//String ruta_foto = "src\\main\\resources\\static\\images";
			String nombre_archivo = file.getOriginalFilename();		

			// Verifica si el directorio existe, si no, créalo
			File directorio = new File(ruta_foto);
			if (!directorio.exists()) {
				directorio.mkdirs();
			}
			
			File archivo = new File(ruta_foto +"\\"+ nombre_archivo);
			file.transferTo(archivo);
			
			objeto_entidad.setFoto(nombre_archivo);
			producto_repo.save(objeto_entidad);

		} catch (Exception e) {
			System.out.println("ERROR: FALLO AL MANEJAR EL ARCHIVO");
			e.printStackTrace();
		}
		
		return "redirect:/productos";
	}
	
	@RequestMapping("/irAModificarProducto/{id}")
	public String irAModificarProducto(@PathVariable int id, Model model) {
		
		Optional<Producto> producto_seleccionado = producto_repo.findById(id);
		
		model.addAttribute("atr_producto_seleccionado", producto_seleccionado);
		
		return "modificar";
	}
	
	@RequestMapping("modificarProducto")
	public String modificarProducto(@ModelAttribute Producto atr_producto_seleccionado) {
		
		producto_repo.save(atr_producto_seleccionado);
		
		return "redirect:/productos";
	}
	
	@RequestMapping("/anadirProductoATicket/{id}")
	public String anadirProductoATicket(HttpSession session, @PathVariable int id, Model model) {
		
		//BUSCAMOS PRODUCTO EN LA BD
		Optional<Producto> producto_añadido = producto_repo.findById(id);
		
		//SACAMOS LOS ATRIBUTOS QUE NECESITAMOS DEL PRODUCTO A VARIABLES
		String id_producto = String.valueOf(id);
		String nombre_producto = producto_añadido.get().getNombre();
		double precio_producto = producto_añadido.get().getPrecio();
		
		//CREAMOS VARIABLE DE CANTIDAD DEL PRODUCTO
		int cantidad_producto = 0;
		
		//CREAMOS VARIABLE DE PRECIO TOTAL 
		double precio_total_producto = 0;
		
		//COMPROBAMOS SI YA EXISTE EL PRODUCTO EN LA SESIÓN
		if (session.getAttribute(id_producto) != null) { //Si el producto ya está guardado en la sesión se suma 1 a la cantidad y se actualiza el precio total

			ProductoParaTicket ppt = (ProductoParaTicket)session.getAttribute(id_producto);
			cantidad_producto = ppt.getCantidad();
			cantidad_producto += 1;
			precio_total_producto = precio_producto * cantidad_producto;
		}
		else { //Si el producto no está guardado en la sesión la cantidad del producto es 1 y el precio total es igual al precio
			cantidad_producto = 1;
			precio_total_producto = precio_producto;
		}
		
		//CREAMOS OBJETO QUE VAMOS A AÑADIR A LA SESIÓN
		ProductoParaTicket producto_para_ticket = new ProductoParaTicket(nombre_producto, precio_producto, cantidad_producto, precio_total_producto, id);
		
		//METEMOS PRODUCTO EN LA SESIÓN
		session.setAttribute(id_producto, producto_para_ticket);
		
		return "redirect:/";
	}
	
	@RequestMapping("/borrarTicket")
	public String borrarTicket(HttpSession session) {
		
		session.invalidate();
		
		return "redirect:/";
	}
	
	@RequestMapping("/quitarProductoDelTicket/{id}")
	public String quitarProductoDelTicket(HttpSession session, @PathVariable int id, Model model) {
		
		System.out.println("PASA POR AQUÍ");
		
		//Pasamos la id a String
		String id_string = Integer.toString(id);
		
		//Guardamos el producto que queremos eliminar en un objeto
		ProductoParaTicket producto_a_eliminar = (ProductoParaTicket)session.getAttribute(id_string);
		
		//Extraemos la cantidad y el precio del producto a eliminar
		int cantidad_producto = producto_a_eliminar.getCantidad();
		double precio_producto = producto_a_eliminar.getPrecio();
		
		//Comprobamos si la cantidad es mayor a 1
		if(cantidad_producto > 1) { //Si es mayor a 1 restamos 1 a la cantidad del objeto, actualizamos el importe total del producto y guardamos el objeto actualizado
			cantidad_producto -= 1;
			double importe_total = cantidad_producto * precio_producto;
			producto_a_eliminar.setCantidad(cantidad_producto);
			producto_a_eliminar.setPrecio_total(importe_total);
			session.setAttribute(id_string, producto_a_eliminar);
		}
		else { //Si es 1, quitamos el producto de la sesion
			session.removeAttribute(id_string);
		}
		
		
		return "redirect:/";
	}
	
	@RequestMapping("/guardarTicket")
	public String guardarTicket(HttpSession session, Model model) {
		
		//CREAMOS TICKET VACÍO
		Ticket nuevo_ticket = new Ticket();
		
		//OBTENEMOS STRING CON LA FECHA ACTUAL
		Date fecha_hora_actual = new Date();
		SimpleDateFormat formato_fecha = new SimpleDateFormat("yyyy/MM/dd");
		String fecha_actual_formateada = formato_fecha.format(fecha_hora_actual);
		
		//OBTENEMOS STRING CON LA HORA ACTUAL
		SimpleDateFormat formato_hora = new SimpleDateFormat("HH:mm");
		String hora_atual_formateada = formato_hora.format(fecha_hora_actual);
		
		//INCLUIMOS FECHA Y HORA EN EL TICKET
		nuevo_ticket.setFecha(fecha_actual_formateada);
		nuevo_ticket.setHora(hora_atual_formateada);
		
		//GUARDAMOS TICKET EN LA BD
		Ticket ticket_insertado = ticket_repo.save(nuevo_ticket); 
		
		//OBTENEMOS UNA LISTA CON LAS IDS GUARDADAS EN LA SESIÓN
		Enumeration<String> ids_en_sesion = session.getAttributeNames(); 
		List<String> lista_ids_en_sesion = Collections.list(ids_en_sesion); 
		
		//CREAMOS UNA LISTA VACÍA PARA GUARDAR LA INFO DE LOS PRODUCTOS EN SESIÓN
		List<ProductoParaTicket> lista_productos_en_sesion = new ArrayList<ProductoParaTicket>();
		
		//RECORREMOS LA LISTA DE IDS EN SESIÓN Y POR CADA ID...
		for (String id_en_sesion : lista_ids_en_sesion) {
			
			//...guardamos la info del producto en una variable...
			ProductoParaTicket info_producto = (ProductoParaTicket) session.getAttribute(id_en_sesion);
			
			//...y añadimos esa info en la lista de productos en sesión
			lista_productos_en_sesion.add(info_producto);
		}
		
		//CREAMOS LISTA VACÍA PARA METER LOS PRODUCTOS QUE IRÁN AL TICKET/PRODUCTO (SOLO ID Y CANTIDAD DEL PRODUCTO)
		List<TicketProducto> lista_productos_en_ticket = new ArrayList<TicketProducto>();
		
		//COMPROBAMOS SI LA SESIÓN ESTÁ VACÍA
		if(!lista_ids_en_sesion.isEmpty()) { 
			//Si no está vacía recorremos la lista de productos en la sesión,...  
			for (ProductoParaTicket producto : lista_productos_en_sesion) {
				
				//...extraemos id, cantidad del producto e importe total,...
				int id_producto = producto.getId();
				Producto pr = producto_repo.getById(id_producto);
				int cantidad_producto = producto.getCantidad();
				double importe_total = producto.getPrecio_total();
				
				//...los metemos en un obtjeto con formato de TICKET/PRODUCTO...
				TicketProducto producto_para_ticket= new TicketProducto(ticket_insertado, pr, cantidad_producto, importe_total);
				
				//...añadimos este ticket
				lista_productos_en_ticket.add(producto_para_ticket);
			}
		
		}
		
		//RECORREMOS LA LISTA DE LOS PRODUCTOS QUE QUEREMOS GUARDAR EN EL TICKET
		for (TicketProducto producto : lista_productos_en_ticket) {
			//Guardamos el producto en el ticket en la BD
			ticket_producto_repo.save(producto); 
		}
		
		//BORRAMOS LA SESIÓN 
		session.invalidate();
		
		GestorMensajes.ponerMensaje(1, session);
		
		return "redirect:/";
	}
	
	@RequestMapping("/verTicket/{id}")
	public String verTicket(@PathVariable int id, Model model, HttpSession session) {
		
		//OBTENEMOS DATOS DEL TICKET QUE QUEREMOS VER
		Ticket ticket_seleccionado = ticket_repo.getById(id);
		
		//METEMOS EL TICKET SELECCIONADO EN UN ATR PARA MANDAR A LA VISTA
		session.setAttribute("ticket_seleccionado", ticket_seleccionado);
		model.addAttribute("atr_ticket_seleccionado", ticket_seleccionado);
		System.out.println("TICKET SELECCIONADO DESDE verTicket: " + model.getAttribute("atr_ticket_seleccionado"));
		
		//OBTENEMOS LISTA DE PRODUCTOS DEL TICKET QUE QUEREMOS VER USAR LA FUNCIÓN CREADA EN EL REPOSITORY
		List<TicketProducto> lista_productos_del_ticket = ticket_producto_repo.obtenerProductosPorTicketId(id);
		System.out.println("LISTA PRODUCTOS EN TICKET: " + lista_productos_del_ticket);
		
		
		//METEMOS LA LISTA EN UN ATR PARA MANDAR A LA VISTA
		model.addAttribute("atr_lista_productos_del_ticket", lista_productos_del_ticket);
		
		return "redirect:/tickets";
	}

}
