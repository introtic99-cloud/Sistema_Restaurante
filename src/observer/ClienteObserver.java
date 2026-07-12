package observer;

public class ClienteObserver implements Observer {
    private String nombreCliente;
    
    public ClienteObserver(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
    
    @Override
    public void actualizar(String mensaje) {
        System.out.println("Cliente " + nombreCliente + " notificado: " + mensaje);
    }
}