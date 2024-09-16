package com.example.todolist.controller;

import com.example.todolist.repository.TodoRepositoryInMemory;
import com.example.todolist.service.TodoService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.empty;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest {
    @Autowired
    MockMvc mockMvc;


    @TestConfiguration
    static class TodoControllerTestConfig {
        @Bean
        public TodoRepositoryInMemory todoRepositoryInMemory() {
            TodoRepositoryInMemory todoRepositoryInMemory = new TodoRepositoryInMemory();
            todoRepositoryInMemory.generateTodos();
            return todoRepositoryInMemory;
        }

        @Bean
        public TodoService todoService(TodoRepositoryInMemory todoRepositoryInMemory) {
            return new TodoService(todoRepositoryInMemory);
        }
    }

    @Nested
    class getAllTodos {
        @Test
        void shouldGetAllTodos() throws Exception {
            mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()
                );
        }

        @Test
        void shouldReturnEmptyListWhenNoTodos() throws Exception {
            TodoRepositoryInMemory.getTodoList().clear();
            mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
        }
    }

    @Nested
    class getTodoById {
        @Test
        void shouldGetTodoById() throws Exception {
            mockMvc.perform(get("/todos/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2)
                );
        }

        @Test
        void shouldReturnNotFoundWhenTodoIdNotExists() throws Exception {
            mockMvc.perform(get("/todos/10"))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    class getTodoByTitle {
        @Test
        void shouldReturnAListTodoByTitle() throws Exception {
            mockMvc.perform(get("/todos/search")
                    .param("title", "An title_2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("An title_2")
                );
        }

        @Test
        void shouldReturnNotFoundWhenTodoTitleNotExists() throws Exception {
            mockMvc.perform(get("/todos/search")
                    .param("title", "An title_10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    class getIsCompletedTodos {
        @Test
        void shouldReturnAllCompletedTodos() throws Exception {
            mockMvc.perform(get("/todos/status")
                    .param("completed", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        }

        @Test
        void shouldReturnAllNotCompletedTodos() throws Exception {
            mockMvc.perform(get("/todos/status")
                    .param("completed", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    class createTodo {
        @Test
        void shouldCreateTodo() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                    .post("/todos/create")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                            "title": "Limpar PC",
                            "description": "Limpeza preventiva do PC"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Limpar PC"))
                .andExpect(jsonPath("$.description").value("Limpeza preventiva do PC"))
                .andExpect(jsonPath("$.createdAt").exists());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            """
                {"title": "An title"}
                """,
            "{}"
        })
        void shouldReturnBadRequestWhenMissingFields(String content) throws Exception {
            mockMvc.perform(post("/todos/create")
                    .contentType(APPLICATION_JSON)
                    .content(content))
                .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "",
            " ",
            "{t: Incorrect field}",
            "{'title': Incorrect field}",
            "{'title': \"Incorrect field\"}",
            "{\"title\": 'An Title', \"description\":}"
        })
        void shouldReturnBadRequestWhenContentIsIncorrect(String content) throws Exception {
            mockMvc.perform(post("/todos/create")
                    .contentType(APPLICATION_JSON)
                    .content(content))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class updateTodo {
        @Test
        void shouldUpdateTodo() throws Exception {
            mockMvc.perform(put("/todos/3")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                            "title": "New Title",
                            "description": "New Description"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.description").value("New Description"))
                .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        void shouldUpdateTodoWithOneField() throws Exception {
            mockMvc.perform(put("/todos/3")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                            "description": "New Description"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.description").value("New Description"))
                .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        void shouldReturnNotFoundWhenTryUpdateANonExistentTodo() throws Exception {
            mockMvc.perform(put("/todos/10")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                            "title": "New Title",
                            "description": "New Description"
                        }
                        """))
                .andExpect(status().isNotFound());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "",
            " ",
            "{t: Incorrect field}",
            "{'title': Incorrect field}",
            "{'title': \"Incorrect field\"}",
            "{\"title\": 'An Title', \"description\":}"
        })
        void shouldReturnBadRequestWhenContentIsIncorrect(String content) throws Exception {
            mockMvc.perform(put("/todos/3")
                    .contentType(APPLICATION_JSON)
                    .content(content))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class deleteTodo {
        @Test
        void shouldDeleteTodo() throws Exception {
            mockMvc.perform(delete("/todos/1"))
                .andExpect(status().isNoContent());

            mockMvc.perform(get("/todos/1"))
                .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnNotFoundWhenTryDeleteANonExistentTodo() throws Exception {
            mockMvc.perform(delete("/todos/10"))
                .andExpect(status().isNotFound());
        }
    }
}
