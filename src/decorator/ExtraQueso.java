package decorator;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author USER
 */
import model.Plato;

public class ExtraQueso extends PlatoDecorator {
    private static final double PRECIO_EXTRA = 3.0;
    
    public ExtraQueso(Plato plato) {
        super(plato);
    }
    
    @Override
    public double getPrecioExtra() {
        return PRECIO_EXTRA;
    }
    
    @Override
    public String getDescripcionExtra() {
        return "Extra Queso";
    }
    
    @Override
    public String getDescripcion() {
        return platoDecorado.getDescripcion() + " + " + getDescripcionExtra();
    }
}