package com.technokratos.mapper.api.ticket;

import com.technokratos.dto.request.ticket.TicketFullRequest;
import com.technokratos.dto.response.ticket.TicketFullResponse;
import com.technokratos.mapper.api.Mapper;
import com.technokratos.model.Ticket;

public interface TicketFullMapper extends Mapper<TicketFullRequest, Ticket, TicketFullResponse> {
}
