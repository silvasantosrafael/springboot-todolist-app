package com.example.todolist.service;

import com.example.todolist.model.Todo;
import com.example.todolist.repository.BaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class TodoService {
    BaseRepository baseRepository;


    public TodoService(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;
    }

    public List<Todo> getAll() {
        return this.baseRepository.findAll();
    }

    public Optional<Todo> getById(Long id) {
        return this.baseRepository.findById(id);
    }

    public List<Todo> getByTitle(String title) {
        return this.baseRepository.findByTitle(title);
    }

    public List<Todo> getByStatusCompleted(boolean completed) {
        return this.baseRepository.findByCompleted(completed);
    }

    public Todo save(Todo todo) {
        return this.baseRepository.save(todo);
    }

    public Todo update(Todo todo, Long id) {
        return baseRepository.findById(id)
            .map(foundTodo -> {
                if (todo.getTitle() != null) {
                    foundTodo.setTitle(todo.getTitle());
                }

                if (todo.getDescription() != null) {
                    foundTodo.setDescription(todo.getDescription());
                }

                if (todo.isCompleted() != foundTodo.isCompleted()) {
                    foundTodo.setCompleted(todo.isCompleted());
                }

                return baseRepository.update(foundTodo);
            })
            .orElse(null);
    }

    public boolean delete(Long id) {
        return this.baseRepository.findById(id)
            .map(foundTodo -> {
                this.baseRepository.deleteById(id);
                return true;
            })
            .orElse(false);
    }
}
