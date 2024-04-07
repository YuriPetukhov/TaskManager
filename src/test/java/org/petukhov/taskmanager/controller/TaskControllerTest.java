package org.petukhov.taskmanager.controller;

import org.junit.jupiter.api.*;
import org.opentest4j.AssertionFailedError;
import org.petukhov.taskmanager.dto.CreateTaskDTO;
import org.petukhov.taskmanager.dto.TaskInfoDTO;
import org.petukhov.taskmanager.entity.Task;
import org.petukhov.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import testData.TestData;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class TaskControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    TaskController taskController;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;
    private CreateTaskDTO createTaskDTO;
    private TaskInfoDTO taskInfoDTO;
    private Task task;


    @BeforeEach
    void setup() {
        createTaskDTO = TestData.randomTestDataCreateTaskDTO();
        taskInfoDTO = TestData.randomTestDataTaskInfoDTO();
        task = TestData.randomTestDataTask();
    }

    @AfterEach
    void cleanup() {
        taskRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        org.assertj.core.api.Assertions.assertThat(taskController).isNotNull();
    }

    @Test
    @DisplayName("Test create a new task - successful")
    void createTask() {
        ResponseEntity<Void> response = testRestTemplate.postForEntity("/tasks", createTaskDTO, Void.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @DisplayName("Test create a new task with empty name - unsuccessful")
    public void createTask_EmptyNameUnsuccessful() {
        createTaskDTO.setTitle("");

        assertThrows(AssertionFailedError.class, () -> {
            ResponseEntity<String> response = testRestTemplate.exchange("/tasks", HttpMethod.POST, new HttpEntity<>(createTaskDTO), String.class);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(Objects.requireNonNull(response.getBody()).contains("Validation failed"));
        });
    }

    @Test
    @DisplayName("Test getting task info - successful")
    void getTaskInfo() {
        taskRepository.save(task);
        Task savedTask = taskRepository.save(task);
        Long taskId = savedTask.getId();

        ResponseEntity<TaskInfoDTO> response = testRestTemplate.getForEntity("/tasks/{taskId}", TaskInfoDTO.class, taskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Test getting task info - unsuccessful when task not found")
    public void getTaskInfo_Unsuccessful() {
        Long taskId = 1L;
        ResponseEntity<String> response = testRestTemplate.getForEntity("/tasks/{taskId}", String.class, taskId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Test getting all tasks info - successful")
    void getAllTasks() {
        taskRepository.save(task);
        taskInfoDTO.setTitle(task.getTitle());
        taskInfoDTO.setDescription(task.getDescription());
        taskInfoDTO.setCompleted(false);
        Integer page = 1;
        Integer size = 10;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<List<TaskInfoDTO>> response = testRestTemplate.exchange(
                "/tasks?page={page}&size={size}",
                HttpMethod.GET, entity,
                new ParameterizedTypeReference<>() {
                }, page, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Test getting tasks info - empty list")
    public void getAllTasks_EmptyList() {
        Integer page = 1;
        Integer size = 10;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<List<TaskInfoDTO>> response = testRestTemplate.exchange(
                "/tasks?page={page}&size={size}",
                HttpMethod.GET, entity,
                new ParameterizedTypeReference<>() {
                }, page, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
    }

    @Test
    @DisplayName("Test deleting task - successful")
    public void deleteTask_Successful() {
        taskRepository.save(task);
        Task savedTask = taskRepository.save(task);
        Long taskId = savedTask.getId();

        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/tasks/{taskId}", HttpMethod.DELETE, null, Void.class, taskId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Test deleting task - unsuccessful when task not found")
    public void deleteTask_Unsuccessful() {
        Long taskId = 999L;

        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/tasks/{taskId}", HttpMethod.DELETE, null, Void.class, taskId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    @DisplayName("Test updating task info - successful")
    public void updateTaskInfo_Successful() {
        taskRepository.save(task);
        Task savedTask = taskRepository.save(task);
        Long taskId = savedTask.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TaskInfoDTO> requestEntity = new HttpEntity<>(taskInfoDTO, headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/tasks/{taskId}", HttpMethod.PUT, requestEntity, Void.class, taskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Test updating task info - unsuccessful when task not found")
    public void updateTaskInfo_Unsuccessful() {
        Long taskId = 200L;
        task.setId(1L);
        taskRepository.save(task);
        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/tasks/{taskId}", HttpMethod.PUT, new HttpEntity<>(taskInfoDTO), Void.class, taskId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


}