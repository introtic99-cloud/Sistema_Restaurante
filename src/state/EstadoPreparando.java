package state;

import model.Pedido;

public class EstadoPreparando implements EstadoPedido {
    @Override
    public void siguienteEstado(Pedido pedido) {
        pedido.cambiarEstado(new EstadoListo());
        System.out.println("Pedido #" + pedido.getId() + " LISTO para servir");
    }
    
    @Override
    public void cancelarPedido(Pedido pedido) {
        pedido.cambiarEstado(new EstadoCancelado());
        System.out.println("Pedido #" + pedido.getId() + " CANCELADO durante preparación");
    }
    
    @Override
    public String getNombreEstado() {
        return "PREPARANDO";
    }
}