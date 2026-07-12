package services;

import memento.HistorialMenu;
import memento.Menu;
import model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio en memoria (sin base de datos) con los datos maestros
 * de la pollería: clientes, personal, mesas y carta (platos).
 *
 * @author USER
 */
public class DatosService {

    private final List<Cliente> clientes;
    private final List<Empleado> empleados; // Mesero, Cocinero, Administrador (Liskov)
    private final List<Mesa> mesas;
    private final Menu menu;               // Originador del Memento
    private final HistorialMenu historialMenu; // Cuidador (caretaker) del Memento

    public DatosService() {
        clientes = new ArrayList<>();
        empleados = new ArrayList<>();
        mesas = new ArrayList<>();
        menu = new Menu();
        historialMenu = new HistorialMenu();
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        // Clientes de ejemplo
        clientes.add(new Cliente(1, "Juan Pérez", "12345678", "999888777", "juan@email.com"));
        clientes.add(new Cliente(2, "María Gómez", "87654321", "988777666", "maria@email.com"));

        // Personal del local: distintos tipos de Empleado usados de forma
        // polimórfica (principio de sustitución de Liskov: cualquier
        // subtipo de Empleado puede usarse donde se espera un Empleado).
        empleados.add(new Mesero(1, "Carlos López", "11223344", 1500, "MAÑANA", 5));
        empleados.add(new Mesero(2, "Ana Torres", "22334455", 1500, "TARDE", 5));
        empleados.add(new Mesero(3, "Luis Ramos", "33445566", 1500, "NOCHE", 5));
        empleados.add(new Cocinero(4, "Rosa Medina", "44556677", 1800, "MAÑANA", "Pollo a la brasa"));
        empleados.add(new Cocinero(5, "Pedro Vargas", "55667788", 1800, "TARDE", "Broaster y frituras"));
        empleados.add(new Administrador(6, "Sofía Ramírez", "66778899", 2500, "MAÑANA", "TOTAL"));

        // Mesas del local
        mesas.add(new Mesa(1, 4, "Salón Principal"));
        mesas.add(new Mesa(2, 2, "Salón Principal"));
        mesas.add(new Mesa(3, 4, "Terraza"));
        mesas.add(new Mesa(4, 6, "Terraza"));
        mesas.add(new Mesa(5, 4, "Área Familiar"));
        mesas.add(new Mesa(6, 8, "Área Familiar"));
        mesas.add(new Mesa(7, 2, "Barra"));
        mesas.add(new Mesa(8, 4, "Salón Principal"));

        // Carta de la pollería (gestionada por el Originador del Memento)
        menu.agregarPlato(new Plato(1, "Pollo a la Brasa 1/4", 18.50, "Cuarto de pollo dorado a la brasa", "POLLOS A LA BRASA"));
        menu.agregarPlato(new Plato(2, "Pollo a la Brasa 1/2", 32.00, "Medio pollo dorado a la brasa", "POLLOS A LA BRASA"));
        menu.agregarPlato(new Plato(3, "Pollo a la Brasa Entero", 60.00, "Pollo entero dorado a la brasa", "POLLOS A LA BRASA"));
        menu.agregarPlato(new Plato(4, "Combo Personal", 22.00, "1/4 de pollo + papas + gaseosa 355ml", "COMBOS"));
        menu.agregarPlato(new Plato(5, "Combo Familiar", 72.00, "Pollo entero + papas + ensalada + gaseosa 1.5L", "COMBOS"));
        menu.agregarPlato(new Plato(6, "Broaster 1/4", 19.50, "Presa de pollo apanada estilo broaster", "BROASTER"));
        menu.agregarPlato(new Plato(7, "Alitas BBQ (8 unid.)", 24.00, "Alitas bañadas en salsa BBQ", "ALITAS Y CHICHARRÓN"));
        menu.agregarPlato(new Plato(8, "Chicharrón de Pollo", 26.00, "Trozos de pollo apanados y fritos", "ALITAS Y CHICHARRÓN"));
        menu.agregarPlato(new Plato(9, "Papas Fritas (porción)", 9.00, "Papas fritas crocantes", "ACOMPAÑAMIENTOS"));
        menu.agregarPlato(new Plato(10, "Arroz Chaufa de Pollo", 18.00, "Arroz chaufa con trozos de pollo", "ACOMPAÑAMIENTOS"));
        menu.agregarPlato(new Plato(11, "Ensalada Criolla", 7.00, "Cebolla, tomate y limón", "ENSALADAS"));
        menu.agregarPlato(new Plato(12, "Ensalada Mixta", 8.00, "Lechuga, tomate y verduras frescas", "ENSALADAS"));
        menu.agregarPlato(new Plato(13, "Gaseosa 500ml", 6.00, "Bebida gaseosa personal", "BEBIDAS"));
        menu.agregarPlato(new Plato(14, "Gaseosa 1.5L", 10.00, "Bebida gaseosa para compartir", "BEBIDAS"));
        menu.agregarPlato(new Plato(15, "Chicha Morada 1L", 9.00, "Refresco de maíz morado", "BEBIDAS"));
        menu.agregarPlato(new Plato(16, "Mazamorra Morada", 6.00, "Postre tradicional peruano", "POSTRES"));
    }

    // Clientes
    public List<Cliente> getClientes() { return clientes; }

    public Cliente agregarCliente(String nombre, String dni, String telefono, String email) {
        int id = clientes.size() + 1;
        Cliente cliente = new Cliente(id, nombre, dni, telefono, email);
        clientes.add(cliente);
        return cliente;
    }

    // Personal (Empleado: Mesero, Cocinero, Administrador -> Liskov)
    public List<Empleado> getEmpleados() { return empleados; }

    public List<Mesero> getMeseros() {
        List<Mesero> meseros = new ArrayList<>();
        for (Empleado e : empleados) {
            if (e instanceof Mesero) meseros.add((Mesero) e);
        }
        return meseros;
    }

    // Mesas
    public List<Mesa> getMesas() { return mesas; }

    public List<Mesa> getMesasLibres() {
        List<Mesa> libres = new ArrayList<>();
        for (Mesa m : mesas) {
            if (!m.isOcupada()) libres.add(m);
        }
        return libres;
    }

    // --- Carta (patrón Memento) ---
    public List<Plato> getMenu() { return menu.getPlatos(); }

    public void agregarPlatoACarta(Plato plato) {
        historialMenu.guardar(menu);   // snapshot antes de modificar
        menu.agregarPlato(plato);
    }

    public void quitarPlatoDeCarta(Plato plato) {
        historialMenu.guardar(menu);   // snapshot antes de modificar
        menu.eliminarPlato(plato);
    }

    public boolean deshacerCambioCarta() {
        if (historialMenu.getCantidadEstados() == 0) return false;
        historialMenu.deshacer(menu);
        return true;
    }

    public int getCantidadEstadosGuardados() {
        return historialMenu.getCantidadEstados();
    }
}