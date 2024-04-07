package org.petukhov.taskmanager.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.petukhov.taskmanager.dto.CreateTaskDTO;
import org.petukhov.taskmanager.dto.TaskInfoDTO;
import org.petukhov.taskmanager.entity.Task;
import org.petukhov.taskmanager.exception.TaskNotFoundException;
import org.petukhov.taskmanager.mapper.TaskMapper;
import org.petukhov.taskmanager.repository.TaskRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import testData.TestData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = TestData.class)
class TaskServiceImplTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

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
    @DisplayName("Test create a new task - successful")
    void createTask() {

        when(taskMapper.toEntityTask(createTaskDTO)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);

        taskService.createTask(createTaskDTO);

        verify(taskRepository, times(1)).save(task);
    }

    @Test
    @DisplayName("Test create a new task with empty name - unsuccessful")
    public void createTask_EmptyNameUnsuccessful() {
        createTaskDTO.setTitle("");
        Task taskWithEmptyTitle = new Task();
        when(taskMapper.toEntityTask(createTaskDTO)).thenReturn(taskWithEmptyTitle);
        doThrow(DataIntegrityViolationException.class).when(taskRepository).save(taskWithEmptyTitle);

        assertThrows(DataIntegrityViolationException.class, () -> taskService.createTask(createTaskDTO));
        verify(taskRepository, times(1)).save(taskWithEmptyTitle);
    }


    @Test
    @DisplayName("Test getting task info - successful")
    void getTaskInfo() {

        Long taskId = 1L;
        task.setId(taskId);

        taskInfoDTO.setTitle(task.getTitle());
        taskInfoDTO.setDescription(task.getDescription());
        taskInfoDTO.setDueDate(task.getDueDate());
        taskInfoDTO.setCompleted(task.isCompleted());

        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.of(task));
        when(taskMapper.toTaskInfoDTO(any(Task.class))).thenReturn(taskInfoDTO);

        TaskInfoDTO info = taskService.getTaskInfo(task.getId());

        assertEquals(task.getTitle(), info.getTitle());
        assertEquals(task.getDescription(), info.getDescription());
        assertEquals(task.getDueDate(), info.getDueDate());
        assertEquals(task.isCompleted(), info.isCompleted());
    }

    @Test
    @DisplayName("Test getting task info - unsuccessful when task not found")
    void getTaskInfo_Unsuccessful() {
        Long taskId = 1L;

        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTaskInfo(taskId);
        });
    }


    @Test
    @DisplayName("Test getting all tasks info - successful")
    void getAllTasks() {

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(task);

        List<TaskInfoDTO> taskInfoDTOs = tasks.stream()
                .map(taskMapper::toTaskInfoDTO)
                .collect(Collectors.toList());

        when(taskRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(tasks));

        List<TaskInfoDTO> allTasks = taskService.getAllTasks(1, 10);

        assertEquals(taskInfoDTOs, allTasks);
    }

    @Test
    @DisplayName("Test getting tasks info - empty list")
    public void getAllTasks_EmptyList() {
        List<Task> tasks = new ArrayList<>();

        when(taskRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(tasks));

        List<TaskInfoDTO> allTasks = taskService.getAllTasks(1, 10);

        assertTrue(allTasks.isEmpty());
    }



    @Test
    @DisplayName("Test deleting task - successful")
    void deleteTask() {

        Long taskId = 1L;

        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.of(new Task()));

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).delete(any(Task.class));
    }

    @Test
    @DisplayName("Test deleting task - unsuccessful when task not found")
    public void deleteTask_Unsuccessful() {
        Long taskId = 1L;

        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(taskId));

        verify(taskRepository, never()).delete(any(Task.class));
    }



    @Test
    @DisplayName("Test updating task info - successful")
    void updateTaskInfo() {

        Long taskId = 1L;

        task.setId(taskId);

        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.of(task));
        when(taskMapper.toTaskInfoDTO(any())).thenReturn(taskInfoDTO);

        TaskInfoDTO updatedTaskInfo = taskService.updateTaskInfo(taskId, taskInfoDTO);

        assertEquals(taskInfoDTO.getTitle(), updatedTaskInfo.getTitle());
        assertEquals(taskInfoDTO.getDescription(), updatedTaskInfo.getDescription());
        assertEquals(taskInfoDTO.getDueDate(), updatedTaskInfo.getDueDate());
        assertEquals(taskInfoDTO.isCompleted(), updatedTaskInfo.isCompleted());
    }

    @Test
    @DisplayName("Test updating task info - unsuccessful when task not found")
    void updateTaskInfo_Unsuccessful() {
        Long taskId = 1L;

        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTaskInfo(taskId, new TaskInfoDTO()));

        verify(taskMapper, never()).toTaskInfoDTO(any(Task.class));
    }

}