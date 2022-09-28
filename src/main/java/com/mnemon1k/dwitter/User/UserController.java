package com.mnemon1k.dwitter.User;

import com.mnemon1k.dwitter.User.DTO.UserDTO;
import com.mnemon1k.dwitter.User.DTO.UserUpdateDTO;
import com.mnemon1k.dwitter.excaptions.ApiException;
import com.mnemon1k.dwitter.shared.CurrentUser;
import com.mnemon1k.dwitter.shared.GenericResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        Optional<User> user = userService.getUserByUsername(username);

        if (user.isPresent())
            return new UserDTO(user.get());

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, username + " Not Found");
    }

    @PostMapping
    GenericResponse createUser(@RequestBody @Valid User user){
        userService.save(user);
        return new GenericResponse("User saved");
    }

    @PutMapping("/{id:[0-9]+}")
    @PreAuthorize("#id == principal.id")
    UserDTO updateUser(@PathVariable long id, @RequestBody(required = false) UserUpdateDTO userUpdateDTO){
        User user = userService.update(id, userUpdateDTO);
        return new UserDTO(user);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiException handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request){
        ApiException apiException = new ApiException(400, "Validation error", request.getServletPath());
        BindingResult bindingResult = exception.getBindingResult();
        Map<String, String> validationErrors = new HashMap<>();

        for ( FieldError fieldError: bindingResult.getFieldErrors()){
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        apiException.setValidationErrors(validationErrors);

        return apiException;
    }
}
