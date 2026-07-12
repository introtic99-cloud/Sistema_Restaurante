/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package state;

import model.Pedido;

public class EstadoPendiente implements EstadoPedido {
    @Override
    public void siguienteEstado(Pedido pedido) {
        pedido.cambiarEstado(new EstadoPreparando());
        System.out.println("Pedido #" + pedido.getId() + " pasó a PREPARANDO");
    }
    
    @Override
    public void cancelarPedido(Pedido pedido) {
        pedido.cambiarEstado(new EstadoCancelado());
        System.out.println("Pedido #" + pedido.getId() + " CANCELADO");
    }
    
    @Override
    public String getNombreEstado() {
        return "PENDIENTE";
    }
}