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

public class ExtraTocino extends PlatoDecorator {
    private static final double PRECIO_EXTRA = 4.0;
    
    public ExtraTocino(Plato plato) {
        super(plato);
    }
    
    @Override
    public double getPrecioExtra() {
        return PRECIO_EXTRA;
    }
    
    @Override
    public String getDescripcionExtra() {
        return "Extra Tocino";
    }
    
    @Override
    public String getDescripcion() {
        return platoDecorado.getDescripcion() + " + " + getDescripcionExtra();
    }
}