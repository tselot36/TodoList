package com.example.todo;

import com.example.todo.dto.TaskDto;
import com.example.todo.dto.TaskResponse;
import com.example.todo.entity.UserInfo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.todo.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final ModelMapper mapper;

    public TaskServiceImpl(TaskRepository taskRepository, ModelMapper mapper) {
        this.taskRepository = taskRepository;
        this.mapper = mapper;
    }

    @Override
    public TaskDto createTask(TaskDto taskDto, UserInfo user) {
        Task task = mapToEntity(taskDto);
        task.setUser(user);
        Task newTask = taskRepository.save(task);
        return mapToDTO(newTask);
    }

    @Override
    public TaskResponse getAllTasks(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Task> tasks = taskRepository.findAll(pageable);

        List<Task> taskList = tasks.getContent();
        List<TaskDto> taskDtoList = taskList.stream().map(this::mapToDTO).collect(Collectors.toList());

        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setData(taskDtoList);
        taskResponse.setPageNo(tasks.getNumber());
        taskResponse.setPageSize(tasks.getSize());
        taskResponse.setTotalElements(tasks.getTotalElements());
        taskResponse.setTotalPages(tasks.getTotalPages());
        taskResponse.setLast(tasks.isLast());

        return taskResponse;
    }

    @Override
    public TaskDto getTaskById(long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        return mapToDTO(task);
    }

    @Override
    public TaskDto updateTask(TaskDto taskDto, long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        task.setDescription(taskDto.getDescription());
        task.setCompleted(taskDto.isCompleted());
        Task updatedTask = taskRepository.save(task);
        return mapToDTO(updatedTask);
    }

//    @Override
//    public void deleteTaskById(long id) {
//        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
//        taskRepository.delete(task);
//    }

    @Override
    public void deleteTask(long id) {
        Optional<Task> taskToDelete = taskRepository.findById( id);
        if(taskToDelete.isPresent()) {
            taskRepository.delete(taskToDelete.get());
        } else {
            throw new EntityNotFoundException("Task not found or unauthorized to delete");
        }
    }

    @Override
    public List<Task> findByUserId(int id) {
        return taskRepository.findByUserId(id);
    }

    @Override
    public TaskResponse getAllTasksByCompleted(boolean completed, int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Task> tasks;
        if (completed) {
            tasks = taskRepository.findByCompleted(true, pageable);
        } else {
            tasks = taskRepository.findByCompleted(false, pageable);
        }

        List<Task> taskList = tasks.getContent();
        List<TaskDto> taskDtoList = taskList.stream().map(this::mapToDTO).collect(Collectors.toList());

        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setData(taskDtoList);
        taskResponse.setPageNo(tasks.getNumber());
        taskResponse.setPageSize(tasks.getSize());
        taskResponse.setTotalElements(tasks.getTotalElements());
        taskResponse.setTotalPages(tasks.getTotalPages());
        taskResponse.setLast(tasks.isLast());

        return taskResponse;
    }
    @Override
    public TaskDto findTaskById(long id) {
        // Implement the logic to find and return a TaskDto by its ID
        // For example:
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            TaskDto taskDto = new TaskDto();
            taskDto.setId(task.getId());
            taskDto.setTitle(task.getTitle());
            taskDto.setDescription(task.getDescription());
            return taskDto;
        } else {
            return null; // or throw an exception depending on your application's requirements
        }
    }

    private Task mapToEntity(TaskDto taskDto) {
        return mapper.map(taskDto, Task.class);
    }

    private TaskDto mapToDTO(Task task) {
        return mapper.map(task, TaskDto.class);
    }
}