package com.example.todolist.repository;

import com.example.todolist.model.Todo;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class TodoRepositoryInMemory implements BaseRepository {
    @Getter
    private static final List<Todo> todoList = new ArrayList<>();
    private Long pkId = 1L;

    public TodoRepositoryInMemory() {
        for (int i = 1; i <= 3; i++) {
            todoList.add(new Todo(
                pkId++,
                "An title_" + i,
                "An description " + i,
                false,
                LocalDateTime.now()));
        }
    }

    @Override
    public List<Todo> findAll() {
        return new ArrayList<>(todoList);
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return todoList.stream()
            .filter(todo -> Objects.equals(todo.getId(), id))
            .findFirst();
    }

    @Override
    public List<Todo> findByTitle(String title) {
        return todoList.stream()
            .filter(todo -> todo.getTitle().equals(title))
            .toList();
    }

    @Override
    public List<Todo> findByCompleted(boolean completed) {
        return todoList.stream()
            .filter(todo -> todo.isCompleted() == completed)
            .toList();
    }

    @Override
    public Todo save(Todo todo) {
        todo.setId(pkId);
        todo.setCreatedAt(LocalDateTime.now());
        todoList.add(todo);
        incrementPK();

        return todo;
    }

    @Override
    public Todo update(Todo todo) {
        return todoList.stream()
            .filter(t -> Objects.equals(t.getId(), todo.getId()))
            .findFirst()
            .map(foundTodo -> {
                foundTodo.setTitle(todo.getTitle());
                foundTodo.setDescription(todo.getDescription());
                foundTodo.setCompleted(todo.isCompleted());
                return foundTodo;
            })
            .orElse(null);
    }

    @Override
    public void delete(Todo todo) {
        todoList.removeIf(t -> Objects.equals(t.getId(), todo.getId()));
    }

    private void incrementPK() {
        pkId++;
    }

}
