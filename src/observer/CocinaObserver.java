/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package observer;

public class CocinaObserver implements Observer {
    @Override
    public void actualizar(String mensaje) {
        System.out.println("Cocina notificada: " + mensaje);
        // Lógica para actualizar la pantalla de cocina
    }
}