package com.ipartek.messages;

import jakarta.servlet.http.HttpSession;

public class GestorMensajes implements Mensajes{
	
	public static void ponerMensaje(int id_mensaje, HttpSession session) {
		
		switch(id_mensaje) {
		case 1:
			session.setAttribute("mensaje", TICKET_GUARDADO);
			break;
		
		case 2:
			session.setAttribute("mensaje", TICKET_BORRADO);
			break;
			
		case 3:
			session.setAttribute("mensaje", PRODUCTO_EDITADO);
			break;
			
		case 4:
			session.setAttribute("mensaje", PRODUCTO_GUARDADO);
			break;
			
		default:
			session.setAttribute("mensaje", "");
			break;
		}
	}
	
	public static void borrarMensaje(HttpSession session) {
		session.removeAttribute("mensaje");
	}

}
	