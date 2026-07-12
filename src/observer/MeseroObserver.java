/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package observer;

public class MeseroObserver implements Observer {
    private String nombreMesero;
    
    public MeseroObserver(String nombreMesero) {
        this.nombreMesero = nombreMesero;
    }
    
    @Override
    public void actualizar(String mensaje) {
        System.out.println("Mesero " + nombreMesero + " notificado: " + mensaje);
    }
}