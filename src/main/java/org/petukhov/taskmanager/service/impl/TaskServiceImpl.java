package org.petukhov.taskmanager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.petukhov.taskmanager.dto.CreateTaskDTO;
import org.petukhov.taskmanager.dto.TaskInfoDTO;
import org.petukhov.taskmanager.entity.Task;
import org.petukhov.taskmanager.exception.TaskNotFoundException;
import org.petukhov.taskmanager.mapper.TaskMapper;
import org.petukhov.taskmanager.repository.TaskRepository;
import org.petukhov.taskmanager.service.TaskService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с задачами.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    /**
     * Создание задачи.
     *
     * @param createTaskDTO DTO для создания задачи.
     */
    @Override
    public void createTask(CreateTaskDTO createTaskDTO) {
        Task newTask = taskMapper.toEntityTask(createTaskDTO);
        newTask.setCompleted(false);
        taskMapper.toCreateTaskDTO(taskRepository.save(newTask));
    }

    /**
     * Получение информации по задаче по ее ID.
     *
     * @param taskId ID задачи.
     * @return Информация о задаче.
     * @throws TaskNotFoundException Если задача с указанным ID не найдена.
     */
    @Override
    public TaskInfoDTO getTaskInfo(Long taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            return taskMapper.toTaskInfoDTO(taskOpt.get());
        } else {
            throw new TaskNotFoundException("Задача с ID " + taskId + " не найдена");
        }
    }

    /**
     * Получение информации по всем задачам.
     *
     * @param pageNumber Номер страницы.
     * @param pageSize   Размер страницы.
     * @return Список информации о задачах.
     */
    @Override
    public List<TaskInfoDTO> getAllTasks(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        List<Task> tasks = taskRepository.findAll(pageRequest).getContent();
        return tasks.stream()
                .map(taskMapper::toTaskInfoDTO)
                .collect(Collectors.toList());
    }

    /**
     * Удаление задачи.
     *
     * @param taskId ID задачи.
     * @throws TaskNotFoundException Если задача с указанным ID не найдена.
     */
    @Override
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new TaskNotFoundException("Задача с ID " + taskId + " не найдена"));
        taskRepository.delete(task);
    }

    /**
     * Обновление информации о задаче.
     *
     * @param taskId       ID задачи.
     * @param updatedTaskInfoDTO DTO для обновления информации о задаче.
     * @return Информация об обновленной задаче.
     * @throws TaskNotFoundException Если задача с указанным ID не найдена.
     */
    @Override
    public TaskInfoDTO updateTaskInfo(Long taskId, TaskInfoDTO updatedTaskInfoDTO) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Задача с id " + taskId + " не найдена"));

        task.setDescription(updatedTaskInfoDTO.getDescription());
        task.setTitle(updatedTaskInfoDTO.getTitle());
        task.setDueDate(updatedTaskInfoDTO.getDueDate());
        task.setCompleted(updatedTaskInfoDTO.isCompleted());

        return taskMapper.toTaskInfoDTO(taskRepository.save(task));
    }
}

