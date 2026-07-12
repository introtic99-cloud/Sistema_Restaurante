/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.Pedido to edit this template
 */
package model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import state.EstadoPedido;
import state.EstadoPendiente;
/**
 *
 * @author USER
 */
public class Pedido {
     private int id;
    private Cliente cliente;
    private Mesero mesero;
    private Mesa mesa;
    private List<DetallePedido> detalles;
    private EstadoPedido estado;
    private LocalDateTime fechaHora;
    
    public Pedido(int id, Cliente cliente, Mesero mesero, Mesa mesa) {
        this.id = id;
        this.cliente = cliente;
        this.mesero = mesero;
        this.mesa = mesa;
        this.detalles = new ArrayList<>();
        this.estado = new EstadoPendiente();
        this.fechaHora = LocalDateTime.now();
    }
    
    public void agregarDetalle(DetallePedido detalle) {
        detalles.add(detalle);
    }
    
    public void cambiarEstado(EstadoPedido nuevoEstado) {
        this.estado = nuevoEstado;
    }
    
    public double calcularTotal() {
        return detalles.stream()
                .mapToDouble(DetallePedido::calcularSubtotal)
                .sum();
    }
    
    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Mesero getMesero() { return mesero; }
    public Mesa getMesa() { return mesa; }
    public List<DetallePedido> getDetalles() { return detalles; }
    public EstadoPedido getEstado() { return estado; }
    public LocalDateTime getFechaHora() { return fechaHora; }

    @Override
    public String toString() {
        return "Pedido #" + id + " - " + cliente.getNombre() + " - Mesa " + mesa.getNumero()
                + " (" + estado.getNombreEstado() + ")";
    }
}
