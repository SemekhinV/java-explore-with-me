package ru.practicum.ewm.event.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.exception.EntityNotFoundException;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventPublicFilters;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.enums.EventSortState;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.EventSearchingProvider;
import ru.practicum.ewm.request.service.RequestService;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.HitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.event.enums.EventState.PUBLISHED;
import static ru.practicum.ewm.util.TimeFormatter.DATE_TIME_FORMATTER;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class EventUserServiceImpl implements EventUserService {

    private final EventRepository repository;

    private final EventMapper mapper;

    private final StatsClient client;

    private final EventSearchingProvider provider;

    private final RequestService requestService;

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {

        Event event = repository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Событие с id=" + eventId + " не существует"));

        if (!PUBLISHED.equals(event.getState())) {

            throw new EntityNotFoundException("Запрашиваемое событие еще не опубликовано.");
        }

        var result = mapper.toDto(event);

        addView(result);

        sendStats(request);

        requestService.setConfirmedRequestCountFull(List.of(result));

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getWithParametersByUser(EventPublicFilters filters) {

        var events = provider.getUserFilters(filters);

        var result = mapper.toDtoList(events);

        if (result.isEmpty()) {

            return new ArrayList<>();
        }

        requestService.setConfirmedRequestCountFull(result);

        if (filters.getAvailable() != null) {

            result = result.stream()
                    .filter(event -> event.getConfirmedRequests() < event.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        if (filters.getSort() != null) {

            if (EventSortState.EVENT_DATE.equals(filters.getSort())) {

                result = result.stream()
                        .sorted(Comparator.comparing(EventFullDto::getEventDate))
                        .collect(Collectors.toList());
            } else {

                result = result.stream()
                        .sorted(Comparator.comparing(EventFullDto::getViews))
                        .collect(Collectors.toList());
            }
        }

        sendStats(filters.getRequest());

        return result;
    }

    private void addView(EventFullDto event) {

        var start = event.getCreatedOn() == null ?
                LocalDateTime.now().format(DATE_TIME_FORMATTER)
                : event.getCreatedOn().format(DATE_TIME_FORMATTER);

        var end = LocalDateTime.now().format(DATE_TIME_FORMATTER);

        var stats = client.getStats(start, end, List.of("/events/" + event.getId()), true);

        if (stats != null && !stats.isEmpty()) {

            event.setViews(stats.get(0).getHits());
        } else {

            event.setViews(0L);
        }
    }

    private void sendStats(HttpServletRequest request) {

        var now = LocalDateTime.now().format(DATE_TIME_FORMATTER);

        var requestDto = new HitDto(null, request.getRemoteAddr(), request.getRequestURI(), "ewm-main-service",
                now);

        client.addStats(requestDto);
    }
}
