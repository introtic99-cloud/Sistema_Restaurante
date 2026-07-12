package services;

import interfaces.IReporteService;
import java.time.LocalDateTime;
import model.*;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class ReporteService implements IReporteService {
    @Override
    public void generarReporteVentas() {
        System.out.println("=== REPORTE DE VENTAS ===");
        System.out.println("Fecha: " + LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        System.out.println("=========================");
    }
    
    public void generarReportePedidos(List<Pedido> pedidos) {
        System.out.println("=== PEDIDOS DEL DÍA ===");
        for (Pedido pedido : pedidos) {
            System.out.println("Pedido #" + pedido.getId() + 
                             " - Estado: " + pedido.getEstado().getNombreEstado() +
                             " - Total: S/." + pedido.calcularTotal());
        }
    }
    
    public void generarReporteInventario(Inventario inventario) {
        System.out.println("=== REPORTE DE INVENTARIO ===");
        for (Ingrediente ing : inventario.getIngredientes().values()) {
            System.out.println(ing.getNombre() + ": " + 
                             ing.getCantidad() + " " + 
                             ing.getUnidadMedida() + 
                             " - Valor: S/." + 
                             (ing.getCantidad() * ing.getPrecioUnitario()));
        }
    }
}