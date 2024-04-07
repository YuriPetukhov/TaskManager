package org.petukhov.taskmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.petukhov.taskmanager.dto.CreateTaskDTO;
import org.petukhov.taskmanager.dto.TaskInfoDTO;
import org.petukhov.taskmanager.entity.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    CreateTaskDTO toCreateTaskDTO(Task task);

    Task toEntityTask(CreateTaskDTO createTaskDTO);
    @Mapping(source = "id", target = "taskId")
    TaskInfoDTO toTaskInfoDTO(Task task);
}
