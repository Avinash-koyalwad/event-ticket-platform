package com.event.tickets.controller;

import com.event.tickets.entity.CreateEventRequest;
import com.event.tickets.entity.dto.CreateEventRequestDto;
import com.event.tickets.entity.dto.CreateEventResponseDto;
import com.event.tickets.entity.modal.Event;
import com.event.tickets.mappers.EventMapper;
import com.event.tickets.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @PostMapping
    public ResponseEntity<CreateEventResponseDto> createEvent(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateEventRequestDto createEventRequestDto) {
        CreateEventRequest createEventRequest = eventMapper.fromDto(createEventRequestDto);
        UUID uuid = UUID.fromString(jwt.getSubject());

        Event event = eventService.createEvent(uuid, createEventRequest);
        CreateEventResponseDto createEventResponseDto = eventMapper.toDto(event);

        return new ResponseEntity<>(createEventResponseDto, HttpStatus.CREATED);
    }
}

