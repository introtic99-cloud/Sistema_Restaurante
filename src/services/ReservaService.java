package services;

import model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaService {
    private List<Reserva> reservas;
    
    public ReservaService() {
        this.reservas = new ArrayList<>();
    }
    
    public Reserva crearReserva(Cliente cliente, Mesa mesa, 
                               LocalDateTime fecha, int personas) {
        int id = reservas.size() + 1;
        Reserva reserva = new Reserva(id, cliente, mesa, fecha, personas);
        reservas.add(reserva);
        
        if (verificarDisponibilidad(mesa, fecha)) {
            reserva.setConfirmada(true);
            mesa.setOcupada(true);
            System.out.println("Reserva confirmada #" + id);
        }
        
        return reserva;
    }
    
    public List<Reserva> getReservas() {
        return reservas;
    }

    private boolean verificarDisponibilidad(Mesa mesa, LocalDateTime fecha) {
        for (Reserva reserva : reservas) {
            if (reserva.getMesa().equals(mesa) && 
                reserva.getFechaHora().equals(fecha) && 
                reserva.isConfirmada()) {
                return false;
            }
        }
        return true;
    }
}