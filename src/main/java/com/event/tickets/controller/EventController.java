package com.event.tickets.controller;

import com.event.tickets.entity.CreateEventRequest;
import com.event.tickets.entity.UpdateEventRequest;
import com.event.tickets.entity.dto.*;
import com.event.tickets.entity.modal.Event;
import com.event.tickets.mappers.EventMapper;
import com.event.tickets.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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
        UUID uuid = parseUserId(jwt);

        Event event = eventService.createEvent(uuid, createEventRequest);
        CreateEventResponseDto createEventResponseDto = eventMapper.toDto(event);

        return new ResponseEntity<>(createEventResponseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<UpdateEventResponseDto> updateEvent(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateEventRequestDto updateEventRequestDto,
            @PathVariable UUID eventId) {
        UpdateEventRequest updateEventRequest = eventMapper.fromDto(updateEventRequestDto);
        UUID uuid = parseUserId(jwt);

        Event updatedEvent = eventService.updateEventForOrganizer(uuid, eventId, updateEventRequest);
        UpdateEventResponseDto updateEventResponseDto = eventMapper.toUpdateEventResponseDto(updatedEvent);

        return new ResponseEntity<>(updateEventResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<ListEventResponseDto>> listEventsForOrganizer(
            @AuthenticationPrincipal Jwt jwt,
            Pageable pageable) {
        UUID organizerId = parseUserId(jwt);
        Page<Event> events = eventService.listEventsForOrganizer(organizerId, pageable);
        Page<ListEventResponseDto> response = events.map(eventMapper::toListEventResponseDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<GetEventDetailsResponseDto> getEventDetails(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID eventId
    ) {
        UUID organizerId = parseUserId(jwt);
        return eventService.getEventByIdAndOrganizerId(organizerId, eventId)
                .map(eventMapper::toGetEventDetailsResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private static UUID parseUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}

