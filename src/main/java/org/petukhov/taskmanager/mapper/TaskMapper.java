package org.petukhov.taskmanager.mapper;

import org.mapstruct.Mapper;
import org.petukhov.taskmanager.dto.CreateTaskDTO;
import org.petukhov.taskmanager.dto.TaskInfoDTO;
import org.petukhov.taskmanager.entity.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    CreateTaskDTO toCreateTaskDTO(Task task);

    Task toEntityTask(CreateTaskDTO createTaskDTO);

    TaskInfoDTO toTaskInfoDTO(Task task);
}
