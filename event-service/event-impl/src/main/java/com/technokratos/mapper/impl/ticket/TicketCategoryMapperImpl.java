package com.technokratos.mapper.impl.ticket;

import com.technokratos.dto.TicketCategoryDTO;
import com.technokratos.mapper.api.ticket.TicketCategoryMapper;
import com.technokratos.model.TicketCategory;
import org.springframework.stereotype.Component;

@Component
public class TicketCategoryMapperImpl implements TicketCategoryMapper {

    @Override
    public TicketCategoryDTO toResponse(TicketCategory entity) {
        return TicketCategoryDTO.valueOf(entity.toString());
    }

    @Override
    public TicketCategory toEntity(TicketCategoryDTO request) {
        return TicketCategory.valueOf(request.toString());
    }

}
