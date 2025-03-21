package com.hrd.roth_sr.model.entity;

import com.hrd.roth_sr.model.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    private Long ticketId;
    private String passengerName;
    private LocalDate travelDate;
    private String sourceStation;
    private String destinationStation;
    private Double price;
    private Boolean paymentStatus;
    private TicketStatus ticketStatus;
    private String seatNumber;
}
