/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory;

import interfaces.IMetodoPago;

public class PagoTarjeta implements IMetodoPago {
    @Override
    public boolean procesarPago(double monto) {
        System.out.println("Procesando pago con tarjeta: S/." + monto);
        return true;
    }
    
    @Override
    public String getMetodo() {
        return "TARJETA";
    }
}