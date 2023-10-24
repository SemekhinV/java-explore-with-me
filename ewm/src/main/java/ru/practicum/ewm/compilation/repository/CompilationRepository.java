package ru.practicum.ewm.compilation.repository;

import ru.practicum.ewm.compilation.entity.Compilation;

import java.util.List;

public interface CompilationRepository {

    List<Compilation> getByPinned(Boolean pinned, Integer from, Integer size);
}
