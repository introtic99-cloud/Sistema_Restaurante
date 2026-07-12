/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author USER
 */
public class Cocinero extends Empleado {
      private String especialidad;
    
    public Cocinero(int id, String nombre, String dni, double salario, String turno, String especialidad) {
        super(id, nombre, dni, salario, turno);
        this.especialidad = especialidad;
    }
    
    @Override
    public void realizarFuncion() {
        System.out.println(getRolDescripcion());
    }

    @Override
    public String getRolDescripcion() {
        return "Cocinero " + nombre + " preparando platos (especialidad: " + especialidad + ")";
    }
    
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
}
