package state;

import model.Pedido;

public class EstadoListo implements EstadoPedido {
    @Override
    public void siguienteEstado(Pedido pedido) {
        pedido.cambiarEstado(new EstadoEntregado());
        System.out.println("Pedido #" + pedido.getId() + " ENTREGADO al cliente");
    }
    
    @Override
    public void cancelarPedido(Pedido pedido) {
        System.out.println("No se puede cancelar un pedido LISTO");
    }
    
    @Override
    public String getNombreEstado() {
        return "LISTO";
    }
}
