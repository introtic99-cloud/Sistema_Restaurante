package memento;

import java.util.Stack;

public class HistorialMenu {
    private Stack<MenuMemento> historial = new Stack<>();
    
    public void guardar(Menu menu) {
        historial.push(menu.guardarEstado());
    }
    
    public void deshacer(Menu menu) {
        if (!historial.isEmpty()) {
            menu.restaurarEstado(historial.pop());
        } else {
            System.out.println("No hay estados anteriores para restaurar");
        }
    }
    
    public int getCantidadEstados() {
        return historial.size();
    }
}