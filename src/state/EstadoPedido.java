package state;

import model.Pedido;

public interface EstadoPedido {
    void siguienteEstado(Pedido pedido);
    void cancelarPedido(Pedido pedido);
    String getNombreEstado();
}
