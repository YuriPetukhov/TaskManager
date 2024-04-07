package org.petukhov.taskmanager.service;

import org.petukhov.taskmanager.dto.CreateTaskDTO;
import org.petukhov.taskmanager.dto.TaskInfoDTO;

import java.util.List;

public interface TaskService {
    void createTask(CreateTaskDTO createTaskDTO);

    TaskInfoDTO getTaskInfo(Long taskId);

    List<TaskInfoDTO> getAllTasks(Integer pageNumber, Integer pageSize);

    void deleteTask(Long taskId);

    TaskInfoDTO updateTaskInfo(Long taskId, TaskInfoDTO createTaskDTO);
}
