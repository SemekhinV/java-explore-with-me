package ru.practicum.ewm.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsUserByName(String name);
}
