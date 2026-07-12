/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;

/**
 *
 * @author USER
 */
public class Factura {
    
 private int id;
    private Pedido pedido;
    private double subtotal;
    private double impuesto;
    private double total;
    private LocalDateTime fechaEmision;
    private String metodoPago;
    
    public Factura(int id, Pedido pedido, double impuesto) {
        this.id = id;
        this.pedido = pedido;
        this.subtotal = pedido.calcularTotal();
        this.impuesto = impuesto;
        this.total = subtotal + (subtotal * impuesto);
        this.fechaEmision = LocalDateTime.now();
    }
    
    public int getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public double getSubtotal() { return subtotal; }
    public double getImpuesto() { return impuesto; }
    public double getTotal() { return total; }
    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
}
