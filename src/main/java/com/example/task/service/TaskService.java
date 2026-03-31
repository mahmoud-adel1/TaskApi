package com.example.task.service;

import com.example.task.dto.request.TaskRequest;
import com.example.task.dto.response.TaskResponse;
import com.example.task.entity.Task;
import com.example.task.entity.User;
import com.example.task.enums.TaskStatus;
import com.example.task.exception.TaskNotFoundException;
import com.example.task.mapper.Mapper;
import com.example.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final UserService userService;
    private final TaskRepository taskRepository;


    public TaskResponse create(TaskRequest taskRequest) {
        Task task = Mapper.mapTaskRequestToTask(taskRequest);
        User user = userService.getUserByUsername(taskRequest.getUsername());
        task.setUser(user);
        Task savedTask = taskRepository.save(task);
        return Mapper.mapTaskToTaskResponse(savedTask);
    }

    public TaskResponse update(Long taskId) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            throw new TaskNotFoundException(taskId + " is not found");
        }
        Task task = optionalTask.get();
        task.setTaskStatus(TaskStatus.DONE);
        Task updatedTask = taskRepository.save(task);
        return Mapper.mapTaskToTaskResponse(updatedTask);
    }

    public List<TaskResponse> getAssignedTasks() {
        User user = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        List<Task> tasks = taskRepository.findTaskByUser(user);
        List<TaskResponse> taskResponses = new ArrayList<>();

        for (Task task : tasks) {
            taskResponses.add(Mapper.mapTaskToTaskResponse(task));
        }
        return taskResponses;
    }

    public void delete(Long taskId) {
        if (taskRepository.findById(taskId).isEmpty()) {
            throw new TaskNotFoundException(taskId + " is not found");
        }
        taskRepository.deleteById(taskId);
    }
}
