/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author USER
 */
public class Mesero extends Empleado {
    private int mesasAsignadas;
    
    public Mesero(int id, String nombre, String dni, double salario, String turno, int mesasAsignadas) {
        super(id, nombre, dni, salario, turno);
        this.mesasAsignadas = mesasAsignadas;
    }
    
    @Override
    public void realizarFuncion() {
        System.out.println(getRolDescripcion());
    }

    @Override
    public String getRolDescripcion() {
        return "Mesero " + nombre + " atendiendo " + mesasAsignadas + " mesas";
    }
    
    public int getMesasAsignadas() { return mesasAsignadas; }
    public void setMesasAsignadas(int mesasAsignadas) { this.mesasAsignadas = mesasAsignadas; }
}
