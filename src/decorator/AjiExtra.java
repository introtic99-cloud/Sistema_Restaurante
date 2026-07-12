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

public class AjiExtra extends PlatoDecorator {
    private static final double PRECIO_EXTRA = 1.0;

    public AjiExtra(Plato plato) {
        super(plato);
    }

    @Override
    public double getPrecioExtra() {
        return PRECIO_EXTRA;
    }

    @Override
    public String getDescripcionExtra() {
        return "Ají Extra";
    }

    @Override
    public String getDescripcion() {
        return platoDecorado.getDescripcion() + " + " + getDescripcionExtra();
    }
}
