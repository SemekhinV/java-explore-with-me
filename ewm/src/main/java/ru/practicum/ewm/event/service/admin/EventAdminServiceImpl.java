package ru.practicum.ewm.event.service.admin;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.error.exception.BadInputParametersException;
import ru.practicum.ewm.error.exception.EntityConflictException;
import ru.practicum.ewm.error.exception.EntityNotFoundException;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.entity.Location;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.EventSearchingProvider;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.ewm.event.enums.EventState.*;
import static ru.practicum.ewm.util.TimeFormatter.DATE_TIME_FORMATTER;

@Service
@Transactional
@AllArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {

    private final EventRepository repository;

    private final CategoryRepository categoryRepository;

    private final EventMapper mapper;

    private final StatsClient client;

    private final EventSearchingProvider searchingProvider;

    private final LocationRepository locationRepository;

    @Override
    public List<EventFullDto> getWithParametersByAdmin(List<Long> users,
                                                       List<EventState> states,
                                                       List<Long> categories,
                                                       LocalDateTime rangeStart,
                                                       LocalDateTime rangeEnd,
                                                       Integer from,
                                                       Integer size) {

        if (rangeEnd != null && rangeStart != null && rangeStart.isAfter(rangeEnd)) {

            throw new BadInputParametersException("Время окончания не может быть раньше времени начала.");
        }

        var events = searchingProvider.getAdminFilters(users, states, categories, rangeStart, rangeEnd, from, size);

        if (events.isEmpty()) {

            return new ArrayList<>();
        }

        var result = mapper.toDtoList(events);

        result.forEach(event -> event.setViews(getViews(event)));

        return result;
    }

    @Override
    public EventFullDto editEventByAdmin(Long eventId, UpdateEventAdminRequest dto) {

        var event = repository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Событие с id = " + eventId + " не найдено."));

        if (dto.getEventDate() != null && dto.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {

            throw new EntityConflictException("Событие не может начинаться ранее часа от текущего времени.");
        }

        if (dto.getStateAction() != null && !PENDING.equals(event.getState())) {

            throw new EntityConflictException("Только находящиеся в состоянии ожидания события доступны к изменению.");
        }

        if (dto.getCategory() != null) {

            var category = categoryRepository.findById(dto.getCategory()).orElseThrow(
                    () -> new EntityNotFoundException("Категория с  id = " + dto.getCategory() + " не найдена"));

            event.setCategory(category);
        }

        if (dto.getLocation() != null) {

            var dtoLocation = dto.getLocation();

            var location = locationRepository.findByLatAndLon(dtoLocation.getLat(), dtoLocation.getLon())
                    .orElseGet(() -> locationRepository.save(
                            new Location(null, dtoLocation.getLat(), dtoLocation.getLon())));

            event.setLocation(location);
        }

        if (dto.getStateAction() != null) {

            switch (dto.getStateAction()) {

                case PUBLISH_EVENT:
                    event.setState(PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;

                case REJECT_EVENT:
                    event.setState(REJECTED);
                    break;

                default:
                    throw new BadInputParametersException("Передано необрабатываемое состояние события.");
            }
        }

        updateEvent(event, dto);

        var result = mapper.toDto(repository.save(event));

        result.setViews(getViews(result));

        return result;
    }

    private void updateEvent(Event event, UpdateEventAdminRequest dto) {

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {

            event.setTitle(dto.getTitle());
        }

        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) {

            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {

            event.setDescription(dto.getDescription());
        }

        if (dto.getPaid() != null) {

            event.setPaid(dto.getPaid());
        }

        if (dto.getRequestModeration() != null) {

            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getParticipantLimit() != null) {

            event.setParticipantLimit(dto.getParticipantLimit());
        }
    }

    private Long getViews(EventFullDto event) {

        var start = event.getCreatedOn() == null ?
                LocalDateTime.now().format(DATE_TIME_FORMATTER)
                : event.getCreatedOn().format(DATE_TIME_FORMATTER);

        var end = LocalDateTime.now().format(DATE_TIME_FORMATTER);

        var stats = client.getStats(start, end, List.of("/events/" + event.getId()), true);

        if (stats != null && !stats.isEmpty()) {

            return stats.get(0).getHits();
        } else {

            return 0L;
        }
    }
}
