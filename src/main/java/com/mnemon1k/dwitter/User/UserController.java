package com.mnemon1k.dwitter.User;

import com.mnemon1k.dwitter.User.DTO.UserDTO;
import com.mnemon1k.dwitter.User.DTO.UserUpdateDTO;
import com.mnemon1k.dwitter.shared.CurrentUser;
import com.mnemon1k.dwitter.shared.GenericResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/1.0/users")
public class UserController {

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    Page<UserDTO> getUsers(@CurrentUser User loggedInUser, @PageableDefault(size = 10) Pageable page) {
        return userService.getAllUsers(loggedInUser, page).map(UserDTO::new);
    }

    @GetMapping("/{username}")
    UserDTO getUsers(@PathVariable String username) {
        User user = userService.getUserByUsername(username);

        return new UserDTO(user);
    }

    @PostMapping
    GenericResponse createUser(@RequestBody @Valid User user){
        userService.save(user);
        return new GenericResponse("User saved");
    }

    @PutMapping("/{id:[0-9]+}")
    @PreAuthorize("#id == principal.id")
    UserDTO updateUser(
            @PathVariable long id,
            @Valid @RequestBody(required = false) UserUpdateDTO userUpdateDTO){
        User user = userService.update(id, userUpdateDTO);
        return new UserDTO(user);
    }
}
