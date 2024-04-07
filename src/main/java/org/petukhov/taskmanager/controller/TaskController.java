package org.petukhov.taskmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.petukhov.taskmanager.dto.CreateTaskDTO;
import org.petukhov.taskmanager.dto.TaskInfoDTO;
import org.petukhov.taskmanager.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/tasks")
@Tag(name = "Задачи")
public class TaskController {

    private final TaskService taskService;

    /**
     * Добавление задачи.
     *
     * @param createTaskDTO DTO для создания задачи.
     * @return 201 Created, если задача успешно создана.
     */
    @PostMapping
    @Operation(summary = "Добавление задачи")
    public ResponseEntity<Void> createTask(@Valid @RequestBody CreateTaskDTO createTaskDTO) {
        taskService.createTask(createTaskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Получение информации по задаче по ее ID.
     *
     * @param taskId ID задачи.
     * @return 200 OK, если задача найдена.
     */
    @GetMapping("/{taskId}")
    @Operation(summary = "Получить информацию по задаче по ее ID")
    public ResponseEntity<TaskInfoDTO> getTaskInfo(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTaskInfo(taskId));
    }

    /**
     * Получение информации по всем задачам.
     *
     * @param pageNumber Номер страницы.
     * @param pageSize   Размер страницы.
     * @return 200 OK, если задачи найдены.
     */
    @GetMapping
    @Operation(summary = "Получить информацию по всем задачам")
    public ResponseEntity<List<TaskInfoDTO>> getAllTasks(@RequestParam(value = "page") Integer pageNumber,
                                                         @RequestParam(value = "size") Integer pageSize) {
        return ResponseEntity.ok(taskService.getAllTasks(pageNumber, pageSize));
    }

    /**
     * Удаление задачи.
     *
     * @param taskId ID задачи.
     * @return 204 No Content, если задача успешно удалена.
     */
    @DeleteMapping(value = "/{taskId}")
    @Operation(summary = "Удаление задачи")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Обновление информации о задаче.
     *
     * @param taskId       ID задачи.
     * @param taskInfoDTO DTO для обновления информации о задаче.
     * @return 200 OK, если задача успешно обновлена.
     */
    @PutMapping(value = "/{taskId}")
    @Operation(summary = "Обновление информации о задаче")
    public ResponseEntity<TaskInfoDTO> updateTaskInfo(@Valid @PathVariable Long taskId, @RequestBody TaskInfoDTO taskInfoDTO) {
        return ResponseEntity.ok(taskService.updateTaskInfo(taskId, taskInfoDTO));
    }
}
