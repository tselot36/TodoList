package com.example.todo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Data
@Schema(
        description = "TaskDto Model Information"
)
public class TaskDto {
    private long id;
    private Long userId;

    @Schema(
            description = "Task description"
    )
    @NotEmpty
    @Size(min = 2, max = 260, message = "Task description must have 2 characters at least.")
    private String description;

    @Schema(
            description = "Task completed status"
    )
    private boolean completed;

    public Long getUserId() {
        return userId();
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    private Long userId() {
        return userId;
    }

    public String setTitle(String title) {
        return title;
    }
}