package com.lbycpd2.todoexp.restful.user;

import com.lbycpd2.todoexp.restful.user.exceptions.TaskNotFoundException;
import com.lbycpd2.todoexp.restful.user.exceptions.UserNotFoundException;
import com.lbycpd2.todoexp.restful.user.tasks.child.ChildModelAssembler;
import com.lbycpd2.todoexp.restful.user.tasks.child.ChildTask;
import com.lbycpd2.todoexp.restful.user.tasks.parent.ParentModelAssembler;
import com.lbycpd2.todoexp.restful.user.tasks.parent.ParentTask;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
@CrossOrigin(origins = {"*"})
public class UserController {

    private final UserService userService;
    private final UserModelAssembler userModelAssembler;
    private final ParentModelAssembler parentModelAssembler;
    private final ChildModelAssembler childModelAssembler;

    // admin facing

    @GetMapping
    public CollectionModel<EntityModel<User>> getAllUsers(){
        List<EntityModel<User>> users = userService
                .getUsers()
                .stream()
                .map(userModelAssembler::toModel).collect(Collectors.toList());
        return CollectionModel.of(users, linkTo(methodOn(UserService.class).getUsers()).withSelfRel());
    }

    @GetMapping(path="{id}")
    public EntityModel<User> getUser(@PathVariable(name = "id") String id){
        User user = userService.getUser(id);
        return userModelAssembler.toModel(user);
    }

    @PostMapping(path = "{id}")
    public ResponseEntity<String> updateUser(@PathVariable(name = "id") String id,
                                             @RequestBody User user){
        return null;
    }


    @GetMapping(path="{id}/tasks")
    public CollectionModel<EntityModel<ParentTask>> getUserParentTasks(@PathVariable(name = "id") String id){
        User currentUser = userService.getUser(id);
        List<EntityModel<ParentTask>> parentTasks = currentUser
                .getParentTaskList()
                .stream()
                .map(parentModelAssembler::toModel).collect(Collectors.toList());
        return CollectionModel.of(parentTasks, linkTo(methodOn(UserService.class).getParentTasks()).withSelfRel());
    }


    @GetMapping(path = "{id}/{parent_id}")
    public EntityModel<ParentTask> getParentTask(@PathVariable(name="id") String user_id,
                                                 @PathVariable(name = "parent_id") String parent_id) throws TaskNotFoundException, UserNotFoundException {
        ParentTask ptask = userService.getParentTask(user_id, parent_id);
        return parentModelAssembler.toModel(ptask);
    }

    @SneakyThrows
    @GetMapping(path ="{id}/{parent_id}/subtasks")
    public CollectionModel<EntityModel<ChildTask>> getChildTasks(@PathVariable(name = "id") String user_id,
                                                                 @PathVariable(name = "parent_id") String parent_id){
        List<EntityModel<ChildTask>> childrenTasks = userService
                .getParentTask(user_id, parent_id)
                .getChildTasks()
                .stream()
                .map(childModelAssembler::toModel).collect(Collectors.toList());
        return CollectionModel.of(childrenTasks,
                linkTo(methodOn(UserService.class).getChildTasks(user_id, parent_id)).withSelfRel()
        );
    }

    @GetMapping(path="{id}/{parent_id}/{child_id}")
    public EntityModel<ChildTask> getChildTask(@PathVariable(name = "id") String user_id,
                                               @PathVariable(name = "parent_id") String parent_id,
                                               @PathVariable(name = "child_id") String child_id) throws UserNotFoundException, TaskNotFoundException {

        ChildTask childTask = userService.getChildTask(user_id, parent_id, child_id);
        return childModelAssembler.toModel(childTask);
    }

    @PostMapping(path = "/{id}/addparent")
    public ResponseEntity<String> addParentTask(@RequestBody ParentTask parentTask,
                                                 @PathVariable(name = "id") String user_id){
        try {
            userService.addParentTask(userService.getUser(user_id), parentTask);
            return new ResponseEntity<>("Parent task added!", HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("Unable to add parent task", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/{id}/{parent_id}/setStatus")
    public ResponseEntity<String> setStatus(@PathVariable(name = "id") String user_id,
                                            @PathVariable(name = "parent_id") String parent_id){
        try {
            userService.setStatus(userService.getUser(user_id), userService.getParentTask(user_id, parent_id));
            return new ResponseEntity<>("Task status changed", HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("Task status error: unable to change! Maybe task no longer exists?", HttpStatus.CONFLICT);
        }
    }

    @PutMapping(path = "{id}/{parent_id}/setDueDate")
    public ResponseEntity<String> setDueDate(@PathVariable(name = "id") String user_id,
                                                 @PathVariable(name = "parent_id") String parent_id,
                                                 @RequestBody String dateTime){
        try {
            userService.setDeadline(userService.getUser(user_id), userService.getParentTask(user_id, parent_id), dateTime);
            return new ResponseEntity<>("Due date successfully changed.", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("Invalid user", HttpStatus.BAD_REQUEST);
        } catch (TaskNotFoundException e) {
            return new ResponseEntity<>("Invalid task", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "{id}/{parent_id}/")
    public ResponseEntity<String> updateParent(@PathVariable(name = "id") String user_id,
                                           @PathVariable(name = "parent_id") String parent_id,
                                           @RequestBody ParentTask parentTask) {
        try {
            userService.setTitle(userService.getUser(user_id),userService.getParentTask(user_id, parent_id), parentTask.getTitle());
            userService.setDescription(userService.getUser(user_id),userService.getParentTask(user_id, parent_id), parentTask.getDescription());
            return new ResponseEntity<>("Updated!", HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("Unable to update", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "{id}/{parent_id}")
    public ResponseEntity<String> deleteParent(@PathVariable(name = "id") String user_id,
                                               @PathVariable(name = "parent_id") String parent_id) throws UserNotFoundException, TaskNotFoundException {
        userService.deleteParentTask(userService.getUser(user_id), userService.getParentTask(user_id, parent_id));
        return new ResponseEntity<String>("Task deleted", HttpStatus.OK);
    }

    @PostMapping(path = "{id}/{parent_id}/addchild")
    public ResponseEntity<String> addchildTask(@RequestBody ChildTask childTask,
                                                @PathVariable(name = "id") String user_id,
                                               @PathVariable(name = "parent_id") String parent_id){
        try {
            userService.addChildTask(userService.getUser(user_id), userService.getParentTask(user_id, parent_id), childTask);
            return new ResponseEntity<>("Child task added!", HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("Unable to add child task", HttpStatus.BAD_REQUEST);
        }
    }
    // admin facing
}



