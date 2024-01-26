package ru.practicum.ewm.event.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.exception.BadInputParametersException;
import ru.practicum.ewm.error.exception.EntityNotFoundException;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.enums.EventSortState;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.EventSearchingProvider;
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

        sendStats(List.of(result), request);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getWithParametersByUser(String text,
                                                      List<Long> categories,
                                                      Boolean paid,
                                                      LocalDateTime rangeStart,
                                                      LocalDateTime rangeEnd,
                                                      Boolean available,
                                                      EventSortState sort,
                                                      Integer from,
                                                      Integer size,
                                                      HttpServletRequest request) {

        if (rangeEnd != null && rangeStart != null && rangeStart.isAfter(rangeEnd)) {

            throw new BadInputParametersException("Время окончания не может быть раньше времени начала.");
        }

        var events = provider.getUserFilters(size, from, categories, rangeStart, rangeEnd, paid, text);

        var result = mapper.toDtoList(events);

        if (result.isEmpty()) {

            return new ArrayList<>();
        }

        if (available != null) {

            result = result.stream()
                    .filter(event -> event.getConfirmedRequests() < event.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        if (sort != null) {

            if (EventSortState.EVENT_DATE.equals(sort)) {

                result = result.stream()
                        .sorted(Comparator.comparing(EventFullDto::getEventDate))
                        .collect(Collectors.toList());
            } else {

                result = result.stream()
                        .sorted(Comparator.comparing(EventFullDto::getViews))
                        .collect(Collectors.toList());
            }
        }

        sendStats(result, request);

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

    private void sendStats(List<EventFullDto> events, HttpServletRequest request) {

        var now = LocalDateTime.now().format(DATE_TIME_FORMATTER);

        var requestDto = new HitDto(null, request.getRemoteAddr(), "/events", "main", now);

        client.addStats(requestDto);

        events.forEach(event -> sendStatsForTheEvent(event.getId(), request.getRemoteAddr(), now));
    }

    private void sendStatsForTheEvent(Long eventId, String remoteAddress, String timestamp) {

        client.addStats(new HitDto(null, remoteAddress, "/events/" + eventId, "main", timestamp));
    }
}
