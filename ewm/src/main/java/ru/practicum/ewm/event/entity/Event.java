package ru.practicum.ewm.event.entity;

import lombok.*;
import ru.practicum.ewm.category.entity.Category;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String annotation;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, name = "created_on")
    private LocalDateTime createdOn;

    @Column(nullable = false, length = 7000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(nullable = false)
    private Boolean paid;

    @Column(name = "participant_limit", nullable = false)
    private Long participantLimit;

    @Column(name = "published_on", nullable = false)
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column
    @Enumerated(EnumType.STRING)
    private EventState state;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id)
                && Objects.equals(annotation, event.annotation)
                && Objects.equals(title, event.title)
                && Objects.equals(createdOn, event.createdOn)
                && Objects.equals(description, event.description)
                && Objects.equals(category, event.category)
                && Objects.equals(eventDate, event.eventDate)
                && Objects.equals(initiator, event.initiator)
                && Objects.equals(location, event.location)
                && Objects.equals(paid, event.paid)
                && Objects.equals(participantLimit, event.participantLimit)
                && Objects.equals(publishedOn, event.publishedOn)
                && Objects.equals(requestModeration, event.requestModeration)
                && state == event.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, annotation, title, createdOn, description, category, eventDate, initiator, location,
                paid, participantLimit, publishedOn, requestModeration, state);
    }
}


