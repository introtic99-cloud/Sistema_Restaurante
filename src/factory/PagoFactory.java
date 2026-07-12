/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory;

import interfaces.IMetodoPago;

/**
 *
 * @author USER
 */
public class PagoFactory {
      public static IMetodoPago crearMetodoPago(String tipo) {
        switch (tipo.toUpperCase()) {
            case "EFECTIVO":
                return new PagoEfectivo();
            case "TARJETA":
                return new PagoTarjeta();
            case "YAPE":
                return new PagoYape();
            
            default:
                throw new IllegalArgumentException("Método de pago no válido: " + tipo);
        }
    }
}
