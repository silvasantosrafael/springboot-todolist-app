package com.example.todolist.service;

import com.example.todolist.model.Todo;
import com.example.todolist.repository.TodoRepositoryInMemory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    class getAllTodos {
        @Test
        void shouldReturnATodoList() {
            List<Todo> todoList = todoService.getAll();

            assertNotNull(todoList);
            assertFalse(todoList.isEmpty());

            assertTrue(todoList.stream().allMatch(todo ->
                todo.getId() != null &&
                    todo.getTitle() != null &&
                    todo.getDescription() != null &&
                    todo.getCreatedAt() != null
            ));
        }

        @Test
        void shouldReturnAEmptyListWhenThereNoTodo() {
            TodoRepositoryInMemory.getTodoList().clear();
            List<Todo> todoList = todoService.getAll();

            assertNotNull(todoList);
            assertTrue(todoList.isEmpty());
        }
    }

    @Nested
    class getById {
        @Test
        void shouldReturnATodoById() {
            Optional<Todo> todoOptional = todoService.getById(1L);

            assertTrue(todoOptional.isPresent());
            Todo todo = todoOptional.get();

            assertNotNull(todo.getId());
            assertNotNull(todo.getTitle());
            assertNotNull(todo.getDescription());
            assertInstanceOf(Boolean.class, todo.isCompleted());
            assertNotNull(todo.getCreatedAt());

        }

        @Test
        void shouldNotReturnATodoWhenNotFound() {
            Optional<Todo> todoOptional = todoService.getById(5L);

            assertFalse(todoOptional.isPresent());
        }
    }

    @Nested
    class getByTitle {
        @Test
        void shouldReturnATodoByTitle() {
            List<Todo> todoList = todoService.getByTitle("An title_1");

            assertNotNull(todoList);
            assertFalse(todoList.isEmpty());

            assertTrue(todoList.stream().allMatch(todo ->
                todo.getId() != null &&
                    todo.getTitle().equals("An title_1") &&
                    todo.getDescription() != null &&
                    todo.getCreatedAt() != null
            ));
        }

        @Test
        void shouldReturnAEmptyListWhenNotFoundTitle() {
            TodoRepositoryInMemory.getTodoList().clear();
            List<Todo> todoList = todoService.getByTitle("Test");

            assertNotNull(todoList);
            assertTrue(todoList.isEmpty());
        }
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

    @Nested
    class update {
        @Test
        void shouldUpdateTodoFields() {
            Optional<Todo> foundTodoOptional = todoService.getById(1L);
            assertTrue(foundTodoOptional.isPresent());

            Todo foundTodo = foundTodoOptional.get();
            Long id = foundTodo.getId();
            LocalDateTime createdAt = foundTodo.getCreatedAt();

            Todo requestTodo = new Todo(null, "New Title", "New Description", true, LocalDateTime.now());
            Todo updatedTodo = todoService.update(requestTodo, 1L);

            assertNotNull(updatedTodo);
            assertEquals("New Title", updatedTodo.getTitle());
            assertEquals("New Description", updatedTodo.getDescription());
            assertTrue(updatedTodo.isCompleted());
            assertEquals(id, updatedTodo.getId());
            assertEquals(createdAt, updatedTodo.getCreatedAt());
        }

        @Test
        void shouldNotUpdateNullFields() {
            Todo requestTodo = new Todo(null, null, null, true, LocalDateTime.now());
            Optional<Todo> foundTodoOptional = todoService.getById(1L);

            assertTrue(foundTodoOptional.isPresent());

            Todo foundTodo = foundTodoOptional.get();
            Todo updatedTodo = todoService.update(requestTodo, 1L);

            assertNotNull(updatedTodo);

            assertEquals(updatedTodo.getTitle(), foundTodo.getTitle());
            assertEquals(updatedTodo.getDescription(), foundTodo.getDescription());
            assertEquals(updatedTodo.isCompleted(), requestTodo.isCompleted());
            assertEquals(updatedTodo.getCreatedAt(), foundTodo.getCreatedAt());
        }

        @Test
        void shouldReturnNullWhenNotFound() {
            Todo requestTodo = new Todo(null, null, "An Description", true, LocalDateTime.now());

            Todo updatedTodo = todoService.update(requestTodo, 999L);

            assertNull(updatedTodo, "Should return null if todo is not found");
        }
    }

    @Nested
    class delete {
        @Test
        void shouldDeleteTodo() {
            boolean delete = todoService.delete(1L);
            assertTrue(delete);
            assertTrue(todoService.getById(1L).isEmpty());
        }

        @Test
        void shouldReturnFalseWhenTodoNotFound() {
            boolean delete = todoService.delete(999L);
            assertFalse(delete);
        }
    }
}
