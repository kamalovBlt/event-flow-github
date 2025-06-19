package com.technokratos.mapper.api.ticket;

import com.technokratos.dto.request.ticket.TicketsRequest;
import com.technokratos.dto.response.ticket.TicketsResponse;
import com.technokratos.mapper.api.Mapper;
import com.technokratos.model.Ticket;

import java.util.List;

public interface TicketsMapper extends Mapper<TicketsRequest, List<Ticket>, TicketsResponse> {
}
