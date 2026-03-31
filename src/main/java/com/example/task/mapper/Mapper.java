package com.example.task.mapper;

import com.example.task.dto.request.RegisterRequest;
import com.example.task.dto.request.TaskRequest;
import com.example.task.dto.request.UserRequest;
import com.example.task.dto.response.TaskResponse;
import com.example.task.entity.Task;
import com.example.task.entity.User;
import com.example.task.enums.TaskStatus;

public class Mapper {

    public static UserRequest mapRegisterRequestToUserRequest(RegisterRequest registerRequest) {
        return UserRequest
                .builder()
                .name(registerRequest.getName())
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .build();
    }

    public static User mapUserRequestToUser(UserRequest userRequest) {
        return User
                .builder()
                .name(userRequest.getName())
                .username(userRequest.getUsername())
                .password(userRequest.getPassword())
                .build();
    }

    public static Task mapTaskRequestToTask(TaskRequest taskRequest) {
        return Task
                .builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .taskStatus(TaskStatus.OPEN)
                .build();
    }

    public static TaskResponse mapTaskToTaskResponse(Task task) {
        return TaskResponse
                .builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .TaskStatus(task.getTaskStatus().name())
                .username(task.getUser().getUsername())
                .build();
    }

}
