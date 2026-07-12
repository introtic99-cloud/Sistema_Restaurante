package services;

import model.*;
import factory.*;
import interfaces.IMetodoPago;

public class PagoService {
    public Factura procesarPago(Pedido pedido, String metodoPago) {
        // Crear factura
        Factura factura = new Factura(1, pedido, 0.18); // IGV 18%
        
        // Obtener método de pago usando Factory
        IMetodoPago pago = PagoFactory.crearMetodoPago(metodoPago);
        
        // Procesar pago
        boolean pagoExitoso = pago.procesarPago(factura.getTotal());
        
        if (pagoExitoso) {
            factura.setMetodoPago(metodoPago);
            System.out.println("Pago exitoso - Factura #" + factura.getId());
            System.out.println("Total: S/." + factura.getTotal());
        }
        
        return factura;
    }
}