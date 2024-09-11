package com.example.todolist.service;

import com.example.todolist.model.Todo;
import com.example.todolist.repository.TodoRepositoryInMemory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TodoServiceTest {
    TodoRepositoryInMemory repository;
    TodoService todoService;

    @BeforeEach
    void setUp() {
        repository = new TodoRepositoryInMemory();
        todoService = new TodoService(repository);
    }

    @Nested
    class getByStatusCompleted {
        @Test
        void shouldGetAllCompletedTodos() {
            List<Todo> completedTodos = todoService.getByStatusCompleted(true);

            assertTrue(completedTodos.stream().allMatch(Todo::isCompleted));
        }

        @Test
        void shouldGetAllNotCompletedTodos() {
            List<Todo> completedTodos = todoService.getByStatusCompleted(false);

            assertFalse(completedTodos.stream().allMatch(Todo::isCompleted));
        }

    }

    @Nested
    class save {
        @Test
        void shouldCreateATodo() {
            Todo todo = new Todo(4L, "A title Test", "A description Test", true, LocalDateTime.now());
            Todo newTodo = todoService.save(todo);

            assertNotNull(newTodo.getId());
            assertNotNull(newTodo.getCreatedAt());
        }
    }
}
