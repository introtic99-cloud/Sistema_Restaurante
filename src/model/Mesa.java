/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author USER
 */
public class Mesa {
    private int numero;
    private int capacidad;
    private String ubicacion;
    private boolean ocupada;
    private long horaOcupacion;
    
    public Mesa(int numero, int capacidad, String ubicacion) {
        this.numero = numero;
        this.capacidad = capacidad;
        this.ubicacion = ubicacion;
        this.ocupada = false;
        this.horaOcupacion = 0L;
    }
    
    public int getNumero() { return numero; }
    public int getCapacidad() { return capacidad; }
    public String getUbicacion() { return ubicacion; }
    public boolean isOcupada() { return ocupada; }

    public void setOcupada(boolean ocupada) {
        if (ocupada && !this.ocupada) {
            this.horaOcupacion = System.currentTimeMillis();
        } else if (!ocupada) {
            this.horaOcupacion = 0L;
        }
        this.ocupada = ocupada;
    }

    /** Segundos transcurridos desde que la mesa fue ocupada (0 si está libre). */
    public long getSegundosOcupada() {
        if (!ocupada || horaOcupacion == 0L) return 0L;
        return Math.max(0L, (System.currentTimeMillis() - horaOcupacion) / 1000L);
    }

    @Override
    public String toString() {
        return "Mesa " + numero + " (" + ubicacion + ")";
    }
}
