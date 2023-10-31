package ru.practicum.ewm.user.entity;

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
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 140)
    private String name;

    @Column(name = "email", length = 140, unique = true)
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return this.getId() != null && Objects.equals(this.getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email);
    }
}
