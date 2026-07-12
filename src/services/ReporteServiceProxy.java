package services;

import interfaces.IReporteService;

/**
 * PATRÓN PROXY (protección / control de acceso).
 *
 * Envuelve al ReporteService real y sólo delega la llamada si el usuario
 * actual tiene permisos de administrador. Así, el objeto real nunca es
 * accedido directamente por el resto de la aplicación (Facade), lo que
 * respeta también el principio de Inversión de Dependencia (D-SOLID),
 * ya que el Facade trabaja únicamente contra la interfaz IReporteService.
 *
 * @author USER
 */
public class ReporteServiceProxy implements IReporteService {

    private final ReporteService reporteServiceReal;
    private boolean modoAdministrador;

    public ReporteServiceProxy() {
        this.reporteServiceReal = new ReporteService();
        this.modoAdministrador = false;
    }

    public void setModoAdministrador(boolean modoAdministrador) {
        this.modoAdministrador = modoAdministrador;
    }

    public boolean isModoAdministrador() {
        return modoAdministrador;
    }

    @Override
    public void generarReporteVentas() {
        if (!modoAdministrador) {
            System.out.println("[Proxy] Acceso denegado: se requiere modo administrador "
                    + "para generar reportes.");
            throw new SecurityException(
                    "Acceso denegado: active el modo administrador para generar reportes.");
        }
        System.out.println("[Proxy] Acceso concedido. Delegando al ReporteService real...");
        reporteServiceReal.generarReporteVentas();
    }
}
