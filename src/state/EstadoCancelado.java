package state;

import model.Pedido;

public class EstadoCancelado implements EstadoPedido {
    @Override
    public void siguienteEstado(Pedido pedido) {
        System.out.println("No se puede cambiar el estado de un pedido CANCELADO");
    }
    
    @Override
    public void cancelarPedido(Pedido pedido) {
        System.out.println("El pedido #" + pedido.getId() + " ya está cancelado");
    }
    
    @Override
    public String getNombreEstado() {
        return "CANCELADO";
    }
}