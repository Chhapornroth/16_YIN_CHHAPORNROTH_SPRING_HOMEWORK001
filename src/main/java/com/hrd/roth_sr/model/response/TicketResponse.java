package com.hrd.roth_sr.model.response;

import com.hrd.roth_sr.model.entity.Ticket;
import com.hrd.roth_sr.model.enums.TicketStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.Optional;

@Data
public class TicketResponse {
    private Long ticketId;
    private String passengerName;
    private LocalDate travelDate;
    private String sourceStation;
    private String destinationStation;
    private Double price;
    private Boolean paymentStatus;
    private TicketStatus ticketStatus;
    private String seatNumber;

    public TicketResponse(Ticket ticket){
        ticketId = ticket.getTicketId();
        passengerName = ticket.getPassengerName();
        travelDate = ticket.getTravelDate();
        sourceStation = ticket.getSourceStation();
        destinationStation = ticket.getDestinationStation();
        price = ticket.getPrice();
        paymentStatus = ticket.getPaymentStatus();
        ticketStatus = ticket.getTicketStatus();
        seatNumber = ticket.getSeatNumber();
    }
}
