package interfaces;

/**
 * Abstracción del servicio de reportes.
 * Permite que RestauranteFacade dependa de esta interfaz (Inversión de
 * Dependencia) en lugar de la clase concreta, y habilita el uso de un
 * Proxy de control de acceso (patrón Proxy) sin modificar al cliente.
 *
 * @author USER
 */
public interface IReporteService {
    void generarReporteVentas();
}
