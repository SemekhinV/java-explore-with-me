package ru.practicum.ewm.request.entity;

import lombok.*;
import ru.practicum.ewm.request.enums.RequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 140)
    private RequestStatus status;

    @Column(name = "event")
    private Long event;

    @Column(name = "requester")
    private Long requester;

    @Column(name = "created")
    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        Request request = (Request) o;
        return id.equals(request.id)
                && status == request.status
                && event.equals(request.event)
                && requester.equals(request.requester)
                && created.equals(request.created);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}