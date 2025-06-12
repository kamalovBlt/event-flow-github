package com.technokratos.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    private Long userId;
    private Long eventId;
    private String locationId;
    private Long hallId;
    private Long rowId;
    private Long seatId;
    private Integer categoryId;
    private BigDecimal cost;
    private boolean deleted;

    private TicketId id;

    public void postLoad() {
        this.id = new TicketId(userId, eventId, locationId, hallId, rowId, seatId);
    }
}
