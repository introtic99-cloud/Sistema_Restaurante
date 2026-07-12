package state;

import model.Pedido;

public class EstadoEntregado implements EstadoPedido {
    @Override
    public void siguienteEstado(Pedido pedido) {
        System.out.println("El pedido #" + pedido.getId() + " ya fue entregado");
    }
    
    @Override
    public void cancelarPedido(Pedido pedido) {
        System.out.println("No se puede cancelar un pedido ENTREGADO");
    }
    
    @Override
    public String getNombreEstado() {
        return "ENTREGADO";
    }
}
