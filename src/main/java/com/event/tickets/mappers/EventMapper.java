package com.event.tickets.mappers;

import com.event.tickets.entity.CreateEventRequest;
import com.event.tickets.entity.CreateTicketTypeRequest;
import com.event.tickets.entity.dto.CreateEventRequestDto;
import com.event.tickets.entity.dto.CreateEventResponseDto;
import com.event.tickets.entity.dto.CreateTicketTypeRequestDto;
import com.event.tickets.entity.modal.Event;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    CreateTicketTypeRequest fromDto(CreateTicketTypeRequestDto dto);

    CreateEventRequest fromDto(CreateEventRequestDto dto);

    CreateEventResponseDto toDto(Event event);
}
