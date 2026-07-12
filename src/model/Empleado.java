/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author USER
 */
public abstract class Empleado {
      protected int id;
    protected String nombre;
    protected String dni;
    protected double salario;
    protected String turno;
    
    public Empleado(int id, String nombre, String dni, double salario, String turno) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.salario = salario;
        this.turno = turno;
    }
    
    public abstract void realizarFuncion();

    /**
     * Descripción textual de la función del empleado. Cada subtipo la
     * implementa de forma coherente con el contrato de Empleado, por lo
     * que cualquier subtipo puede sustituir a Empleado sin sorpresas
     * (principio de sustitución de Liskov).
     */
    public abstract String getRolDescripcion();
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }
    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    @Override
    public String toString() {
        return nombre;
    }
}
