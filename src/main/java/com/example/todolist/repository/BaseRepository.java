package com.example.todolist.repository;

import com.example.todolist.model.Todo;

import java.util.List;
import java.util.Optional;


public interface BaseRepository {

    List<Todo> findAll();

    Optional<Todo> findById(Long id);

    List<Todo> findByTitle(String title);

    List<Todo> findByCompleted(boolean completed);

    Todo save(Todo todo);

    Todo update(Todo todo);

    void delete(Todo todo);
}
