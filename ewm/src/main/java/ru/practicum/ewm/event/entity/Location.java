package ru.practicum.ewm.event.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lat")
    private float lat;

    @Column(name = "lon")
    private float lon;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return this.getId() != null && Objects.equals(this.getId(), location.getId());    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lat, lon);
    }
}
