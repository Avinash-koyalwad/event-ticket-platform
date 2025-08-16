package com.event.tickets.controller;

import com.event.tickets.entity.dto.GetPublishedEventDetailsResponseDto;
import com.event.tickets.entity.dto.ListPublishedEventResponseDTO;
import com.event.tickets.mappers.EventMapper;
import com.event.tickets.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/published-events")
@RequiredArgsConstructor
public class PublishedEventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping
    public ResponseEntity<Page<ListPublishedEventResponseDTO>> listPublishedEvents(Pageable pageable) {

        return ResponseEntity.ok(
                eventService.listEvents(pageable)
                        .map(eventMapper::toListPublishedEventResponseDTO)
        );
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<GetPublishedEventDetailsResponseDto> getPublishedEventById(
            @PathVariable UUID eventId) {
        return eventService.getPublishedEventById(eventId)
                .map(eventMapper::toGetPublishedEventDetailsResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
