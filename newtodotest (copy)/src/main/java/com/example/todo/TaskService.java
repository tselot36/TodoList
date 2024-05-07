package com.example.todo;

import com.example.todo.dto.TaskDto;
import com.example.todo.dto.TaskResponse;
import com.example.todo.entity.UserInfo;

import java.util.List;


public interface TaskService {
    TaskDto createTask(TaskDto taskDto, UserInfo user);

    TaskResponse getAllTasks(int pageNo, int pageSize, String sortBy, String sortDir);

    TaskDto getTaskById(long id);

    TaskDto updateTask(TaskDto postDto, long id);

    void deleteTask(long id);

//    void deleteTasksByCompleted(boolean completed);
    List<Task> findByUserId(int id);

    TaskResponse getAllTasksByCompleted(boolean completed, int pageNo, int pageSize, String sortBy, String sortDir);

    TaskDto findTaskById(long id);
}
