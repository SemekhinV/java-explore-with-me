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
@Table(name = "events")
@Builder(toBuilder = true)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annotation", length = 2000)
    private String annotation;

    @Column(name = "confirmed_requests")
    private long confirmedRequests;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "description", length = 7000)
    private String description;

    @OneToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @OneToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    private User initiator;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "participant_limit")
    private long participantLimit;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private EventState state;

    @Column(name = "title")
    private String title;

    @Column(name = "views")
    private Long views;

    public Event(Long id,
                 String annotation,
                 Long confirmedRequests,
                 LocalDateTime createdOn,
                 String description,
                 Category category,
                 LocalDateTime eventDate,
                 User initiator,
                 Location location,
                 Boolean paid,
                 Long participantLimit,
                 LocalDateTime publishedOn,
                 Boolean requestModeration,
                 EventState state,
                 String title,
                 Long views) {
        this.id = id;
        this.annotation = annotation;
        this.confirmedRequests = confirmedRequests;
        this.createdOn = Objects.requireNonNullElse(createdOn, LocalDateTime.now());
        this.description = description;
        this.category = category;
        this.eventDate = eventDate;
        this.initiator = initiator;
        this.location = location;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.requestModeration = Objects.requireNonNullElse(requestModeration, true);
        this.state = Objects.requireNonNullElse(state, EventState.PENDING);
        this.title = title;
        this.views = views;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return this.getId() != null && Objects.equals(this.getId(), event.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}


