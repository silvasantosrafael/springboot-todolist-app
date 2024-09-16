package com.example.todolist.repository;

import com.example.todolist.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TodoJPARepository extends JpaRepository<Todo, Long> {
    List<Todo> findByCompleted(boolean isCompleted);

    List<Todo> findByTitleIgnoreCase(String title);
}
