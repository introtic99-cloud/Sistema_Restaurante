/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package observer;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject {
    private List<Observer> observadores = new ArrayList<>();
    
    public void agregarObserver(Observer observer) {
        observadores.add(observer);
    }
    
    public void eliminarObserver(Observer observer) {
        observadores.remove(observer);
    }
    
    public void notificarObservers(String mensaje) {
        for (Observer observer : observadores) {
            observer.actualizar(mensaje);
        }
    }
}
