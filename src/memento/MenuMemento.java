package memento;

import model.Plato;
import java.util.List;

public class MenuMemento {
    private List<Plato> platos;
    private double precioTotal;
    
    public MenuMemento(List<Plato> platos, double precioTotal) {
        this.platos = platos;
        this.precioTotal = precioTotal;
    }
    
    public List<Plato> getPlatos() {
        return platos;
    }
    
    public double getPrecioTotal() {
        return precioTotal;
    }
}