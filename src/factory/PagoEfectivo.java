/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory;

/**
 *
 * @author USER
 */
import interfaces.IMetodoPago;

public class PagoEfectivo implements IMetodoPago {
    @Override
    public boolean procesarPago(double monto) {
        System.out.println("Procesando pago en efectivo: S/." + monto);
        return true;
    }
    
    @Override
    public String getMetodo() {
        return "EFECTIVO";
    }
}
