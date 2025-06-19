package com.technokratos.mapper.api.ticket;

import com.technokratos.dto.request.ticket.TicketRequest;
import com.technokratos.dto.response.ticket.TicketResponse;
import com.technokratos.mapper.api.Mapper;
import com.technokratos.model.Ticket;

public interface TicketMapper extends Mapper<TicketRequest, Ticket, TicketResponse> {
}
