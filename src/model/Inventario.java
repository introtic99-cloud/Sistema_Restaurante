/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author USER
 */
public class Inventario {
      private Map<Integer, Ingrediente> ingredientes;
    
    public Inventario() {
        this.ingredientes = new HashMap<>();
    }
    
    public void agregarIngrediente(Ingrediente ingrediente) {
        ingredientes.put(ingrediente.getId(), ingrediente);
    }
    
    public boolean verificarDisponibilidad(int idIngrediente, int cantidad) {
        Ingrediente ingrediente = ingredientes.get(idIngrediente);
        return ingrediente != null && ingrediente.getCantidad() >= cantidad;
    }
    
    public void reducirStock(int idIngrediente, int cantidad) {
        Ingrediente ingrediente = ingredientes.get(idIngrediente);
        if (ingrediente != null) {
            ingrediente.setCantidad(ingrediente.getCantidad() - cantidad);
        }
    }
    
    public Map<Integer, Ingrediente> getIngredientes() {
        return ingredientes;
    }
}
