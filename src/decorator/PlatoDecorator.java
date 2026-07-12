/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package decorator;

/**
 *
 * @author USER
 */
import model.Plato;

public abstract class PlatoDecorator extends Plato {
    protected Plato platoDecorado;
    
    public PlatoDecorator(Plato plato) {
        super(plato.getId(), plato.getNombre(), plato.getPrecio(), 
              plato.getDescripcion(), plato.getCategoria());
        this.platoDecorado = plato;
    }
    
    @Override
    public double calcularPrecioFinal() {
        return platoDecorado.calcularPrecioFinal() + getPrecioExtra();
    }
    
    public abstract double getPrecioExtra();
    public abstract String getDescripcionExtra();
}