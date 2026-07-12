/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author USER
 */
public class DetallePedido {
     private Producto producto;
    private int cantidad;
    private String notas;
    private double precioUnitario;
    
    public DetallePedido(Producto producto, int cantidad, String notas) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.notas = notas;
        this.precioUnitario = producto.calcularPrecioFinal();
    }
    
    public double calcularSubtotal() {
        return precioUnitario * cantidad;
    }
    
    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public String getNotas() { return notas; }
    public double getPrecioUnitario() { return precioUnitario; }
}
