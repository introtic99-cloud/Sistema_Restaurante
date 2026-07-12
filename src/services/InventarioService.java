package services;

import model.*;
import java.util.Map;

public class InventarioService {
    private Inventario inventario;
    
    public InventarioService() {
        this.inventario = new Inventario();
        inicializarInventario();
    }
    
    private void inicializarInventario() {
        inventario.agregarIngrediente(new Ingrediente(1, "Pollo", 80, "kg", 8.0));
        inventario.agregarIngrediente(new Ingrediente(2, "Papa", 100, "kg", 2.5));
        inventario.agregarIngrediente(new Ingrediente(3, "Arroz", 60, "kg", 2.5));
        inventario.agregarIngrediente(new Ingrediente(4, "Aceite", 40, "l", 9.0));
        inventario.agregarIngrediente(new Ingrediente(5, "Ají", 15, "kg", 6.0));
        inventario.agregarIngrediente(new Ingrediente(6, "Limón", 20, "kg", 4.0));
        inventario.agregarIngrediente(new Ingrediente(7, "Lechuga", 15, "kg", 3.0));
        inventario.agregarIngrediente(new Ingrediente(8, "Tomate", 20, "kg", 3.5));
        inventario.agregarIngrediente(new Ingrediente(9, "Cebolla", 20, "kg", 2.8));
        inventario.agregarIngrediente(new Ingrediente(10, "Gaseosa", 60, "unid", 5.0));
    }
    
    public void verificarStock() {
        System.out.println("=== INVENTARIO ACTUAL ===");
        for (Ingrediente ing : inventario.getIngredientes().values()) {
            System.out.println(ing.getNombre() + ": " + 
                             ing.getCantidad() + " " + 
                             ing.getUnidadMedida());
        }
    }
    
    public boolean verificarDisponibilidad(int idIngrediente, int cantidad) {
        return inventario.verificarDisponibilidad(idIngrediente, cantidad);
    }

    public Map<Integer, Ingrediente> getIngredientes() {
        return inventario.getIngredientes();
    }

    public void reabastecer(int idIngrediente, int cantidad) {
        Ingrediente ing = inventario.getIngredientes().get(idIngrediente);
        if (ing != null) {
            ing.setCantidad(ing.getCantidad() + cantidad);
        }
    }
}