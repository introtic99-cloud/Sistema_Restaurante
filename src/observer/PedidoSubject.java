package observer;

public class PedidoSubject extends Subject {
    private String estadoPedido;
    
    public void setEstado(String estado) {
        this.estadoPedido = estado;
        notificarObservers("El pedido ahora está: " + estado);
    }
    
    public String getEstado() {
        return estadoPedido;
    }
}