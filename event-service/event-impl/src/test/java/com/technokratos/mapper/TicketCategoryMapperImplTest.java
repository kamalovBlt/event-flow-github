package com.technokratos.mapper;

import com.technokratos.dto.TicketCategoryDTO;
import com.technokratos.mapper.impl.ticket.TicketCategoryMapperImpl;
import com.technokratos.model.TicketCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class TicketCategoryMapperImplTest {

    @InjectMocks
    private TicketCategoryMapperImpl mapper;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldMapEnumToDTO() {
        TicketCategory category = TicketCategory.VIP;
        TicketCategoryDTO dto = mapper.toResponse(category);

        assertEquals(TicketCategoryDTO.VIP, dto);
    }

    @Test
    void shouldMapDTOToEnum() {
        TicketCategoryDTO dto = TicketCategoryDTO.VIP;
        TicketCategory category = mapper.toEntity(dto);

        assertEquals(TicketCategory.VIP, category);
    }
}