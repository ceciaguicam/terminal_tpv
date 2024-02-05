package com.ipartek.model;

public class ProductoParaTicket {
	private String nombre;
	private double precio;
	private int cantidad;
	private double precio_total;
	private int id;
	
	public ProductoParaTicket(String nombre, double precio, int cantidad, double precio_total, int id) {
		super();
		this.nombre = nombre;
		this.precio = precio;
		this.cantidad = cantidad;
		this.precio_total = precio_total;
		this.id = id;
	}
	
	public ProductoParaTicket() {
		super();
		this.nombre = "";
		this.precio = 0;
		this.cantidad = 0;
		this.precio_total = 0;
		this.id = 0;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public double getPrecio_total() {
		return precio_total;
	}

	public void setPrecio_total(double precio_total) {
		this.precio_total = precio_total;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ProductoParaTicket [nombre=" + nombre + ", precio=" + precio + ", cantidad=" + cantidad
				+ ", precio_total=" + precio_total + ", id=" + id + "]";
	}

	
	
	
}
