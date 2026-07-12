/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author USER
 */
public class Administrador extends Empleado {
     private String nivelAcceso;
    
    public Administrador(int id, String nombre, String dni, double salario, String turno, String nivelAcceso) {
        super(id, nombre, dni, salario, turno);
        this.nivelAcceso = nivelAcceso;
    }
    
    @Override
    public void realizarFuncion() {
        System.out.println(getRolDescripcion());
    }

    @Override
    public String getRolDescripcion() {
        return "Administrador " + nombre + " gestionando el restaurante (acceso: " + nivelAcceso + ")";
    }
    
    public String getNivelAcceso() { return nivelAcceso; }
    public void setNivelAcceso(String nivelAcceso) { this.nivelAcceso = nivelAcceso; }
}
