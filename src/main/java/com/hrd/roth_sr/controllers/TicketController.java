package com.hrd.roth_sr.controllers;

import com.hrd.roth_sr.base.Pagination;
import com.hrd.roth_sr.model.entity.Ticket;
import com.hrd.roth_sr.model.enums.TicketStatus;
import com.hrd.roth_sr.model.request.TicketRequest;
import com.hrd.roth_sr.model.response.TicketResponse;
import com.hrd.roth_sr.response.PagingResponse;
import com.hrd.roth_sr.response.APIResponse;
import com.hrd.roth_sr.utility.Utility;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {
    private List<Ticket> ticketList = new ArrayList<>();
    private final static AtomicLong idCounter = new AtomicLong(5);

    public TicketController() {
        ticketList.addAll(List.of(
                new Ticket(1L, "Sebastian Milder", LocalDate.of(2025, 2, 20), "Station A", "Station B", 120.0, true, TicketStatus.BOOKED, "B3"),
                new Ticket(2L, "Mia Dolan", LocalDate.of(2025, 2, 20), "Station A", "Station B", 120.0, false, TicketStatus.CANCELLED, "B4"),
                new Ticket(3L, "Alex Dunphy", LocalDate.of(2025, 4, 1), "Station C", "Station A", 80.0, true, TicketStatus.BOOKED, "A1"),
                new Ticket(4L, "Luke Dunphy", LocalDate.of(2025, 4, 1), "Station C", "Station A", 90.0, false, TicketStatus.PENDING, "A2")
        ));
    }

    @PostMapping
    public ResponseEntity<APIResponse<TicketResponse>> create(@RequestBody TicketRequest request) {
        if(Utility.isNullOrEmpty(request.getPassengerName()) ||
                request.getTravelDate() == null ||
                Utility.isNullOrEmpty(request.getSourceStation()) ||
                Utility.isNullOrEmpty(request.getDestinationStation()) ||
                request.getPrice() == null ||
                request.getPaymentStatus() == null ||
                Utility.isTicketStatusNotValid(request.getTicketStatus().toString()) ||
                Utility.isNullOrEmpty(request.getSeatNumber())
        ){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new APIResponse<>(false, "Fail to book the ticket.",  HttpStatus.BAD_REQUEST, null));
        }else {
            Ticket ticket = Ticket.builder()
                    .ticketId(idCounter.getAndIncrement())
                    .passengerName(request.getPassengerName())
                    .travelDate(request.getTravelDate())
                    .sourceStation(request.getSourceStation())
                    .destinationStation(request.getDestinationStation())
                    .price(request.getPrice())
                    .paymentStatus(request.getPaymentStatus())
                    .ticketStatus(request.getTicketStatus())
                    .seatNumber(request.getSeatNumber())
                    .build();
            ticketList.add(ticket);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new APIResponse<>(true, "Ticket's been created successfully.", HttpStatus.CREATED, new TicketResponse(ticket)));
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<APIResponse<List<TicketResponse>>> createBulk(@RequestBody List<TicketRequest> list){
        List<Ticket> tickets = new ArrayList<>();
        for(TicketRequest request: list){
            if(Utility.isNullOrEmpty(request.getPassengerName()) ||
                    request.getTravelDate() == null ||
                    Utility.isNullOrEmpty(request.getSourceStation()) ||
                    Utility.isNullOrEmpty(request.getDestinationStation()) ||
                    request.getPrice() == null ||
                    request.getPaymentStatus() == null ||
                    Utility.isTicketStatusNotValid(request.getTicketStatus().toString()) ||
                    Utility.isNullOrEmpty(request.getSeatNumber())
            ){
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<>(false, "Fail to book the tickets.", HttpStatus.BAD_REQUEST, null));
            }
            tickets.add(new Ticket(idCounter.getAndIncrement(), request.getPassengerName(), request.getTravelDate(), request.getSourceStation(), request.getDestinationStation(), request.getPrice(), request.getPaymentStatus(), request.getTicketStatus(), request.getSeatNumber()));
        }
        ticketList.addAll(tickets);
        List<TicketResponse> responses = new ArrayList<>();
        tickets.forEach(ticket -> responses.add(new TicketResponse(ticket)));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new APIResponse<>(true, "Tickets have successfully booked.", HttpStatus.CREATED,responses));
    }

    @GetMapping
    public ResponseEntity<APIResponse<PagingResponse<TicketResponse>>> findAll(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "5") int size
    ) {
        if(page < 1){
            page = 1;
        }
        if(size < 1){
            size = 5;
        }
        List<Ticket> tickets = ticketList
                .stream()
                .skip((long) (page - 1) * size)
                .limit(size)
                .toList();

        int totalTickets = ticketList.size();
        int totalPages = (int) Math.ceil((double) totalTickets / size);
        return ResponseEntity.ok(
                new APIResponse<>(true, "operation was successful.", HttpStatus.OK,
                        new PagingResponse<> (
                                tickets.stream()
                                .map(TicketResponse::new)
                                .toList(),
                                new Pagination(totalTickets, page, size, totalPages))));
    }

    @GetMapping("/{ticket-id}")
    public ResponseEntity<APIResponse<TicketResponse>> findById(@PathVariable("ticket-id") Long id){
        Ticket ticket = ticketList.stream()
                .filter(t -> t.getTicketId().equals(id))
                .findFirst()
                .orElse(null);
        if(ticket != null){
            return ResponseEntity.ok(new APIResponse<>(true, "found", HttpStatus.OK, new TicketResponse(ticket)));
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new APIResponse<>(false, "There is no ticket with ID : " + id, HttpStatus.NOT_FOUND, null));
    }

    @GetMapping("/search")
    public ResponseEntity<APIResponse<List<TicketResponse>>> searchByName (@RequestParam String passengerName){
        List<Ticket> tickets = ticketList
                .stream()
                .filter(ticket -> ticket.getPassengerName().equalsIgnoreCase(passengerName))
                .toList();
        if(tickets.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(false, passengerName + "hasn't been found at all.", HttpStatus.NOT_FOUND, null));
        }
        return ResponseEntity.ok(new APIResponse<>(true, passengerName + "(s) found successfully", HttpStatus.OK, tickets.stream().map(TicketResponse::new).toList()));
    }

    @GetMapping("/filter")
    public ResponseEntity<APIResponse<List<TicketResponse>>> filterByTicketStatusAndTravelDate(
            @RequestParam TicketStatus ticketStatus,
            @RequestParam LocalDate travelDate
    ){
        List<Ticket> tickets = ticketList
            .stream()
            .filter(ticket -> (ticket.getTicketStatus() == ticketStatus && ticket.getTravelDate().equals(travelDate)))
            .toList();
        if(tickets.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(false, "not found", HttpStatus.NOT_FOUND, null));
        }
        return ResponseEntity.ok(new APIResponse<>(true, "Tickets filter successfully", HttpStatus.OK, tickets.stream().map(TicketResponse::new).toList()));
    }

    @PutMapping("/{ticket-id}")
    public ResponseEntity<APIResponse<TicketResponse>> updateById(
            @PathVariable("ticket-id") Long id,
            @RequestBody TicketRequest request
    ){
        Ticket updateTicket = ticketList
                .stream()
                .filter(t -> t.getTicketId().equals(id))
                .findFirst()
                .map(t -> {
                    t.setPassengerName(request.getPassengerName());
                    t.setTravelDate(request.getTravelDate());
                    t.setSourceStation(request.getSourceStation());
                    t.setDestinationStation(request.getDestinationStation());
                    t.setPrice(request.getPrice());
                    t.setPaymentStatus(request.getPaymentStatus());
                    t.setTicketStatus(request.getTicketStatus());
                    t.setSeatNumber(request.getSeatNumber());
                    return t;
                })
                .orElse(null);
        if(updateTicket == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(false, "ID not found", HttpStatus.NOT_FOUND, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new APIResponse<>(true, "updated successfully.", HttpStatus.OK, new TicketResponse(updateTicket)));
    }


    @DeleteMapping("/{ticket-id}")
    public ResponseEntity<APIResponse<Objects>> removeById(@PathVariable("ticket-id") Long id){
        boolean isRemoved = ticketList.removeIf(ticket -> ticket.getTicketId().equals(id));

        if(isRemoved){
            return ResponseEntity.ok(new APIResponse<>(true, "ticket deleted successfully.", HttpStatus.OK, null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(false, "ID not found", HttpStatus.NOT_FOUND, null));
    }
}