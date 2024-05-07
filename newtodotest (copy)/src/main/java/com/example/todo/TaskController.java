package com.example.todo;

import com.example.todo.dto.TaskDto;
import com.example.todo.dto.TaskResponse;
import com.example.todo.service.JwtService;
import com.example.todo.entity.UserInfo;
import com.example.todo.service.UserInfoService;
import com.example.todo.utils.AppConstants;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.NoSuchElementException;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/todos")
@Tag(
        name = "REST API for Task"
)
public class TaskController {

    @Autowired
    UserInfoService service;
    private TaskService taskService;

    @Autowired
    private JwtService jwtService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(
            summary = "Create a task",
            description = "Creates a task with the given details"
    )
    @ApiResponse(
            responseCode = "201",
            description = "201 CREATED"
    )
    @PostMapping("/create")
    public ResponseEntity<TaskDto> createTask(@RequestHeader("Authorization") String token,  @Valid @RequestBody TaskDto taskDto) {
        String username = Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token.replace("Bearer ","")).getBody().getSubject();
        UserInfo user = service.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return new ResponseEntity<>(taskService.createTask(taskDto,user), HttpStatus.CREATED);
    }

    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    private Key getSignKey() {
        byte[] keyBytes= Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Operation(
            summary = "List all tasks",
            description = "Retrieves all tasks"
    )
    @ApiResponse(
            responseCode = "200",
            description = "200 SUCCESS"
    )
    @GetMapping
    public TaskResponse getAllTasks(
            @RequestParam(value = "completed", required = false) String completed,
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        if (completed != null) {
            return taskService.getAllTasksByCompleted(completed.equals("true"), pageNo, pageSize, sortBy, sortDir);
        }
        return taskService.getAllTasks(pageNo, pageSize, sortBy, sortDir);
    }

    @Operation(
            summary = "Get a task by id",
            description = "Retrieves a task by id"
    )
    @ApiResponse(
            responseCode = "200",
            description = "200 SUCCESS"
    )
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable(name = "id") long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @Operation(
            summary = "Update a task",
            description = "Updates a task with the given details"
    )
    @ApiResponse(
            responseCode = "200",
            description = "200 SUCCESS"
    )
    @PutMapping("/update/{id}")
    public ResponseEntity<TaskDto> updateTask(@RequestHeader("Authorization") String token, @Valid @RequestBody TaskDto taskDto, @PathVariable(name = "id") long id) {
        TaskDto taskResponse = taskService.updateTask(taskDto, id);
        return new ResponseEntity<>(taskResponse, HttpStatus.OK);
    }


    @Operation(
            summary = "Delete a task",
            description = "Deletes the corresponding task"
    )
    @ApiResponse(
            responseCode = "200",
            description = "200 SUCCESS"
    )

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, @RequestHeader("Authorization") String token) {
//        String username = jwtService.extractUsernameFromToken(token);
//        UserInfo user = service.findByUsername(username);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

}