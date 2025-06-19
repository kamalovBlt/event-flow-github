package com.technokratos.mapper;

import com.technokratos.dto.EventCategoryDTO;
import com.technokratos.mapper.impl.EventCategoryMapperImpl;
import com.technokratos.model.EventCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class EventCategoryMapperImplTest {
    
    @InjectMocks
    private EventCategoryMapperImpl mapper;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldMapEnumToDTO() {
        EventCategory category = EventCategory.NO_CATEGORY;
        EventCategoryDTO dto = mapper.toResponse(category);

        assertEquals(EventCategoryDTO.NO_CATEGORY, dto);
    }

    @Test
    void shouldMapDTOToEnum() {
        EventCategoryDTO dto = EventCategoryDTO.CONCERT;
        EventCategory category = mapper.toEntity(dto);

        assertEquals(EventCategory.CONCERT, category);
    }
}