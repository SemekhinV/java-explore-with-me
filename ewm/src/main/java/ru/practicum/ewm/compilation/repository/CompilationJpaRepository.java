package ru.practicum.ewm.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.compilation.entity.Compilation;

public interface CompilationJpaRepository extends JpaRepository<Compilation, Long>, CompilationRepository {

}
