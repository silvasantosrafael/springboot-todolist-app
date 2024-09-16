package com.example.todolist.service;

import com.example.todolist.model.Todo;
import com.example.todolist.repository.TodoRepository;
import com.example.todolist.repository.TodoRepositoryInMemory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TodoServiceTest {
    @Mock
    TodoRepository todoRepository;
    @InjectMocks
    TodoService todoService;
    TodoRepositoryInMemory todoRepositoryInMemory;


    @BeforeEach
    void setUp() {
        todoRepositoryInMemory = new TodoRepositoryInMemory();
        todoRepositoryInMemory.generateTodos();
    }

    @AfterEach
    void clean() {
        TodoRepositoryInMemory.getTodoList().clear();
    }

    @Nested
    class getAllTodos {
        @Test
        void shouldReturnATodoList() {
            when(todoRepository.findAll())
                .thenReturn(todoRepositoryInMemory.findAll());

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
            when(todoRepository.findAll())
                .thenReturn(todoRepositoryInMemory.findAll());

            List<Todo> todoList = todoService.getAll();

            assertNotNull(todoList);
            assertTrue(todoList.isEmpty());
        }
    }

    @Nested
    class getById {
        @Test
        void shouldReturnATodoById() {
            Long id = 1L;
            when(todoRepository.findById(any(Long.class)))
                .thenReturn(todoRepositoryInMemory.findById(id));

            Optional<Todo> todoOptional = todoService.getById(id);

            assertTrue(todoOptional.isPresent());
            Todo todo = todoOptional.get();

            assertNotNull(todo.getId());
            assertEquals(id, todo.getId());
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
            String title = "An title_1";
            when(todoRepository.findByTitle(any(String.class)))
                .thenReturn(todoRepositoryInMemory.findByTitle(title));

            List<Todo> todoList = todoService.getByTitle(title);

            assertNotNull(todoList);
            assertFalse(todoList.isEmpty());

            assertTrue(todoList.stream().allMatch(todo ->
                todo.getId() != null &&
                    todo.getTitle().equals(title) &&
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
            boolean completed = true;
            when(todoRepository.findByCompleted(any(Boolean.class)))
                .thenReturn(todoRepositoryInMemory.findByCompleted(completed));

            List<Todo> completedTodos = todoService.getByStatusCompleted(completed);

            assertNotNull(completedTodos);
            assertFalse(completedTodos.isEmpty());
            assertTrue(completedTodos.stream().allMatch(Todo::isCompleted));
        }

        @Test
        void shouldGetAllNotCompletedTodos() {
            boolean completed = false;
            when(todoRepository.findByCompleted(any(Boolean.class)))
                .thenReturn(todoRepositoryInMemory.findByCompleted(completed));

            List<Todo> completedTodos = todoService.getByStatusCompleted(completed);

            assertNotNull(completedTodos);
            assertFalse(completedTodos.isEmpty());
            assertFalse(completedTodos.stream().allMatch(Todo::isCompleted));
        }

        @Test
        void shouldReturnAEmptyListWhenNotFoundTodo() {
            boolean completed = true;
            TodoRepositoryInMemory.getTodoList().removeIf(Todo::isCompleted);
            when(todoRepository.findByCompleted(any(Boolean.class)))
                .thenReturn(todoRepositoryInMemory.findByCompleted(completed));

            List<Todo> completedTodos = todoService.getByStatusCompleted(completed);

            assertNotNull(completedTodos);
            assertTrue(completedTodos.isEmpty());
        }
    }

    @Nested
    class save {
        @Test
        void shouldCreateATodo() {
            Todo todo = new Todo(null, "A title Test", "A description Test", true, null);
            when(todoRepository.save(any(Todo.class)))
                .thenReturn(todoRepositoryInMemory.save(todo));
            Todo newTodo = todoService.save(todo);

            assertNotNull(newTodo.getId());
            assertNotNull(newTodo.getCreatedAt());
        }
    }

    @Nested
    class update {
        @Test
        void shouldUpdateTodoFields() {
            Long id = 1L;
            String title = "New Title";
            String description = "New Description";

            when(todoRepository.findById(any(Long.class)))
                .thenReturn(todoRepositoryInMemory.findById(id));
            Optional<Todo> foundTodoOptional = todoService.getById(id);

            assertTrue(foundTodoOptional.isPresent());
            Todo foundTodo = foundTodoOptional.get();

            Long foundTodoId = foundTodo.getId();
            LocalDateTime foundTodoCreatedAt = foundTodo.getCreatedAt();

            Todo data = new Todo(null, title, description, true, LocalDateTime.now());

            doAnswer(invocation -> {
                Todo dataTodo = invocation.getArgument(0);
                return todoRepositoryInMemory.update(dataTodo);
            }).
                when(todoRepository).update(any(Todo.class));
            Todo updatedTodo = todoService.update(data, id);

            assertNotNull(updatedTodo);
            assertEquals(title, updatedTodo.getTitle());
            assertEquals(description, updatedTodo.getDescription());
            assertTrue(updatedTodo.isCompleted());
            assertEquals(foundTodoId, updatedTodo.getId());
            assertEquals(foundTodoCreatedAt, updatedTodo.getCreatedAt());
        }

        @Test
        void shouldNotUpdateNullFields() {
            Long id = 1L;

            Todo data = new Todo(null, null, null, false, null);
            when(todoRepository.findById(any(Long.class)))
                .thenReturn(todoRepositoryInMemory.findById(id));
            Optional<Todo> foundTodoOptional = todoService.getById(id);

            assertTrue(foundTodoOptional.isPresent());

            Todo foundTodo = foundTodoOptional.get();
            String originalTitle = foundTodo.getTitle();
            String originalDescription = foundTodo.getDescription();
            boolean originalCompleted = foundTodo.isCompleted();
            LocalDateTime originalFoundTodoCreatedAt = foundTodo.getCreatedAt();

            doAnswer(invocation -> {
                Todo todoUpdate = invocation.getArgument(0);
                return todoRepositoryInMemory.update(todoUpdate);
            }).
                when(todoRepository).update(any(Todo.class));
            Todo updatedTodo = todoService.update(data, id);

            assertNotNull(updatedTodo);

            assertEquals(updatedTodo.getTitle(), originalTitle);
            assertEquals(updatedTodo.getDescription(), originalDescription);
            assertEquals(updatedTodo.isCompleted(), originalCompleted);
            assertEquals(updatedTodo.getCreatedAt(), originalFoundTodoCreatedAt);
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
            Long id = 1L;
            doAnswer(invocation -> {
                Long idParam = invocation.getArgument(0);
                return todoRepositoryInMemory.findById(idParam);
            }).when(todoRepository).findById(any(Long.class));

            doAnswer(invocation -> {
                Long idParam = invocation.getArgument(0);
                todoRepositoryInMemory.deleteById(idParam);
                return null;
            }).when(todoRepository).deleteById(id);

            boolean delete = todoService.delete(id);

            assertTrue(delete);
            assertTrue(todoService.getById(id).isEmpty());
        }

        @Test
        void shouldReturnFalseWhenTodoNotFound() {
            Long id = 999L;
            lenient().doAnswer(invocation -> {
                Long idParam = invocation.getArgument(0);
                lenient();
                todoRepositoryInMemory.deleteById(idParam);
                return null;
            }).when(todoRepository).deleteById(any(Long.class));

            boolean delete = todoService.delete(id);

            assertFalse(delete);
        }
    }
}
