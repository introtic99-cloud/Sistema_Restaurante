/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.List;
import java.util.ArrayList;
/**
 *
 * @author USER
 */
public class Plato extends Producto{
     private String categoria;
    private List<Ingrediente> ingredientes;
    private boolean disponible;
    
    public Plato(int id, String nombre, double precio, String descripcion, String categoria) {
        super(id, nombre, precio, descripcion);
        this.categoria = categoria;
        this.ingredientes = new ArrayList<>();
        this.disponible = true;
    }
    
    @Override
    public double calcularPrecioFinal() {
        return precio;
    }
    
    public void agregarIngrediente(Ingrediente ingrediente) {
        ingredientes.add(ingrediente);
    }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public List<Ingrediente> getIngredientes() { return ingredientes; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}

