package com.technokratos.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    private Long id;
    private Long userId;
    private Long eventId;
    private String locationId;
    private String hallId;
    private Long rowNum;
    private Long seatNum;
    private TicketCategory ticketCategory;
    private BigDecimal cost;
    private boolean isSell;
    private boolean deleted;
}
