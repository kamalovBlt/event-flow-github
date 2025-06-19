package com.technokratos.mapper.impl;

import com.technokratos.dto.EventCategoryDTO;
import com.technokratos.mapper.api.EventCategoryMapper;
import com.technokratos.model.EventCategory;
import org.springframework.stereotype.Component;

@Component
public class EventCategoryMapperImpl implements EventCategoryMapper {

    @Override
    public EventCategoryDTO toResponse(EventCategory entity) {
        return EventCategoryDTO.valueOf(entity.name());
    }

    @Override
    public EventCategory toEntity(EventCategoryDTO request) {
        return EventCategory.valueOf(request.name());
    }

}
