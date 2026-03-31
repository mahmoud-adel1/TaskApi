package com.example.task.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String TaskStatus;
    private String username;
}
