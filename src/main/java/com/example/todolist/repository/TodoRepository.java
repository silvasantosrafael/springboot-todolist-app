package com.example.todolist.repository;

import com.example.todolist.model.Todo;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class TodoRepository implements BaseRepository {
    private final TodoJPARepository repository;


    public TodoRepository(TodoJPARepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Todo> findByCompleted(boolean completed) {
        return repository.findByCompleted(completed);
    }

    @Override
    public List<Todo> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Todo> findByTitle(String title) {
        return repository.findByTitleIgnoreCase(title);
    }

    @Override
    public Todo save(Todo todo) {
        return repository.save(todo);
    }

    @Override
    public Todo update(Todo todo) {
        return repository.save(todo);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
