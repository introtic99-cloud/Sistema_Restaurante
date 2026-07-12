package services;

import model.*;
import observer.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoService {
    private List<Pedido> pedidos;
    private PedidoSubject pedidoSubject;
    
    public PedidoService() {
        this.pedidos = new ArrayList<>();
        this.pedidoSubject = new PedidoSubject();
        
        // Configurar observers
        pedidoSubject.agregarObserver(new CocinaObserver());
    }
    
    public Pedido crearPedido(Cliente cliente, Mesero mesero, Mesa mesa) {
        int id = pedidos.size() + 1;
        Pedido pedido = new Pedido(id, cliente, mesero, mesa);
        pedidos.add(pedido);
        
        // Notificar a los observers
        pedidoSubject.agregarObserver(new ClienteObserver(cliente.getNombre()));
        pedidoSubject.agregarObserver(new MeseroObserver(mesero.getNombre()));
        pedidoSubject.setEstado("NUEVO PEDIDO #" + id);
        
        return pedido;
    }
    
    public void agregarDetallePedido(Pedido pedido, Plato plato, int cantidad) {
        DetallePedido detalle = new DetallePedido(plato, cantidad, "");
        pedido.agregarDetalle(detalle);
    }
    
    public void cambiarEstadoPedido(Pedido pedido) {
        pedido.getEstado().siguienteEstado(pedido);
        pedidoSubject.setEstado(pedido.getEstado().getNombreEstado());
    }
    
    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public Pedido getPedidoPorId(int id) {
        for (Pedido p : pedidos) {
            if (p.getId() == id) return p;
        }
        return null;
    }
}