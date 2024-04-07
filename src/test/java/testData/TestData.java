package testData;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.petukhov.taskmanager.dto.CreateTaskDTO;
import org.petukhov.taskmanager.dto.TaskInfoDTO;
import org.petukhov.taskmanager.entity.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TestData {

    public static CreateTaskDTO randomTestDataCreateTaskDTO() {
        CreateTaskDTO createTaskDTO = new CreateTaskDTO();
        Faker faker = new Faker();
        LocalDateTime now = LocalDateTime.now();
        createTaskDTO.setTitle(faker.name().title());
        createTaskDTO.setDescription(faker.lorem().sentence());
        createTaskDTO.setDueDate(now.plusDays(3));
        return createTaskDTO;
    }

    public static TaskInfoDTO randomTestDataTaskInfoDTO() {
        TaskInfoDTO taskInfoDTO = new TaskInfoDTO();
        Faker faker = new Faker();
        LocalDateTime now = LocalDateTime.now();
        taskInfoDTO.setTitle(faker.name().title());
        taskInfoDTO.setDescription(faker.lorem().sentence());
        taskInfoDTO.setDueDate(now.plusDays(3));
        taskInfoDTO.setCompleted(false);
        return taskInfoDTO;
    }

    public static Task randomTestDataTask() {
        Task task = new Task();
        Faker faker = new Faker();
        LocalDateTime now = LocalDateTime.now();
        task.setTitle(faker.name().title());
        task.setDescription(faker.lorem().sentence());
        task.setDueDate(now.plusDays(3));
        task.setCompleted(false);
        return task;
    }
}