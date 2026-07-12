package memento;

import model.Plato;
import java.util.ArrayList;
import java.util.List;

public class Menu {
    private List<Plato> platos;
    private double precioTotal;
    
    public Menu() {
        this.platos = new ArrayList<>();
        this.precioTotal = 0.0;
    }
    
    public void agregarPlato(Plato plato) {
        platos.add(plato);
        precioTotal += plato.getPrecio();
    }
    
    public void eliminarPlato(Plato plato) {
        platos.remove(plato);
        precioTotal -= plato.getPrecio();
    }
    
    public MenuMemento guardarEstado() {
        return new MenuMemento(new ArrayList<>(platos), precioTotal);
    }
    
    public void restaurarEstado(MenuMemento memento) {
        this.platos = new ArrayList<>(memento.getPlatos());
        this.precioTotal = memento.getPrecioTotal();
    }
    
    public List<Plato> getPlatos() {
        return platos;
    }
    
    public double getPrecioTotal() {
        return precioTotal;
    }
    
    @Override
    public String toString() {
        return "Menu{" +
                "platos=" + platos.size() +
                ", precioTotal=" + precioTotal +
                '}';
    }
}