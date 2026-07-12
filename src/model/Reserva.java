/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;

/**
 *
 * @author USER
 */
public class Reserva {
     private int id;
    private Cliente cliente;
    private Mesa mesa;
    private LocalDateTime fechaHora;
    private int numeroPersonas;
    private boolean confirmada;
    
    public Reserva(int id, Cliente cliente, Mesa mesa, LocalDateTime fechaHora, int numeroPersonas) {
        this.id = id;
        this.cliente = cliente;
        this.mesa = mesa;
        this.fechaHora = fechaHora;
        this.numeroPersonas = numeroPersonas;
        this.confirmada = false;
    }
    
    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Mesa getMesa() { return mesa; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public int getNumeroPersonas() { return numeroPersonas; }
    public boolean isConfirmada() { return confirmada; }
    public void setConfirmada(boolean confirmada) { this.confirmada = confirmada; }
}
