/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package facade;

import java.time.LocalDateTime;
import model.Cliente;
import model.Factura;
import model.Mesa;
import model.Mesero;
import model.Pedido;
import model.Plato;
import model.Reserva;
import model.*;
import services.*;
import factory.*;
import singleton.*;
import interfaces.IReporteService;
/**
 *
 * @author USER
 */
public class RestauranteFacade {
     private PedidoService pedidoService;
    private PagoService pagoService;
    private ReservaService reservaService;
    private InventarioService inventarioService;
    private IReporteService reporteService; // Proxy (control de acceso)
    private DatosService datosService;
    
    public RestauranteFacade() {
        this.pedidoService = new PedidoService();
        this.pagoService = new PagoService();
        this.reservaService = new ReservaService();
        this.inventarioService = new InventarioService();
        this.reporteService = new ReporteServiceProxy();
        this.datosService = new DatosService();
    }

    public void setModoAdministrador(boolean activo) {
        if (reporteService instanceof ReporteServiceProxy) {
            ((ReporteServiceProxy) reporteService).setModoAdministrador(activo);
        }
    }

    public boolean isModoAdministrador() {
        return reporteService instanceof ReporteServiceProxy
                && ((ReporteServiceProxy) reporteService).isModoAdministrador();
    }
    
    // Métodos simplificados para el cliente
    public Pedido crearPedido(Cliente cliente, Mesero mesero, Mesa mesa) {
        return pedidoService.crearPedido(cliente, mesero, mesa);
    }
    
    public void agregarPlatoAPedido(Pedido pedido, Plato plato, int cantidad) {
        pedidoService.agregarDetallePedido(pedido, plato, cantidad);
    }
    
    public Factura procesarPago(Pedido pedido, String metodoPago) {
        return pagoService.procesarPago(pedido, metodoPago);
    }
    
    public Reserva hacerReserva(Cliente cliente, Mesa mesa, 
                               LocalDateTime fecha, int personas) {
        return reservaService.crearReserva(cliente, mesa, fecha, personas);
    }
    
    public void verificarInventario() {
        inventarioService.verificarStock();
    }
    
    public void generarReporteVentas() {
        reporteService.generarReporteVentas();
    }

    // --- Accesos adicionales para la interfaz gráfica ---
    public DatosService getDatosService() {
        return datosService;
    }

    public java.util.List<Pedido> getPedidos() {
        return pedidoService.getPedidos();
    }

    public Pedido getPedidoPorId(int id) {
        return pedidoService.getPedidoPorId(id);
    }

    public void avanzarEstadoPedido(Pedido pedido) {
        pedidoService.cambiarEstadoPedido(pedido);
    }

    public void cancelarPedido(Pedido pedido) {
        pedido.getEstado().cancelarPedido(pedido);
    }

    public java.util.List<Reserva> getReservas() {
        return reservaService.getReservas();
    }

    public java.util.Map<Integer, Ingrediente> getIngredientes() {
        return inventarioService.getIngredientes();
    }

    public void reabastecerIngrediente(int idIngrediente, int cantidad) {
        inventarioService.reabastecer(idIngrediente, cantidad);
    }
}
