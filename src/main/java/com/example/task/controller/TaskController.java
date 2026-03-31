package com.example.task.controller;

import com.example.task.dto.request.TaskRequest;
import com.example.task.dto.response.TaskResponse;
import com.example.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest taskRequest) {
        TaskResponse taskResponse = taskService.create(taskRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskResponse);
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponse> update(@PathVariable Long taskId) {
        TaskResponse taskResponse = taskService.update(taskId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskResponse);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAssignedTasks() {
        List<TaskResponse> taskResponseList = taskService.getAssignedTasks();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskResponseList);
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTask(@PathVariable Long taskId) {
        taskService.delete(taskId);
    }
}
