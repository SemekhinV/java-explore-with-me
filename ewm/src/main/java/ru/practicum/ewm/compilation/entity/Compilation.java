package ru.practicum.ewm.compilation.entity;

import lombok.*;
import ru.practicum.ewm.event.entity.Event;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "compilations")
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 140)
    private String title;

    @Column(name = "pinned")
    private Boolean pinned;

    @ManyToMany
    @JoinTable(name = "events_compilations",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    @OrderBy("eventDate")
    @ToString.Exclude
    private Set<Event> events;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Compilation)) return false;
        Compilation that = (Compilation) o;
        return Objects.equals(id, that.id)
                && Objects.equals(title, that.title)
                && Objects.equals(pinned, that.pinned)
                && Objects.equals(events, that.events);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
