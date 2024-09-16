package com.example.todolist.controller;

import com.example.todolist.model.Todo;
import com.example.todolist.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.internalServerError;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;


@RestController
@RequestMapping("/todos")
public class TodoController {
    TodoService todoService;


    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        return ResponseEntity.ok(this.todoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        return this.todoService.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Todo>> getTodoByTitle(@RequestParam String title) {
        return ResponseEntity.ok(this.todoService.getByTitle(title));
    }

    @GetMapping("/status")
    public ResponseEntity<List<Todo>> getTodoByStatus(@RequestParam boolean completed) {
        return ResponseEntity.ok(this.todoService.getByStatusCompleted(completed));
    }

    @PostMapping("/create")
    public ResponseEntity<Todo> createTodo(@Valid @RequestBody Todo todo) {
        Todo newTodo = this.todoService.save(todo);
        if (newTodo == null) {
            return internalServerError().build();
        }

        return status(CREATED).body(newTodo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@RequestBody Todo todo, @PathVariable Long id) {
        Todo updatedTodo = this.todoService.update(todo, id);
        if (updatedTodo == null) {
            return ResponseEntity.notFound().build();
        }

        return ok(updatedTodo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        return todoService.delete(id) ? noContent().build() : notFound().build();
    }
}
