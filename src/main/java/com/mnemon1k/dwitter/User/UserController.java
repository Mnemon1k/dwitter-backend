package com.mnemon1k.dwitter.User;

import com.fasterxml.jackson.annotation.JsonView;
import com.mnemon1k.dwitter.User.DTO.UserDTO;
import com.mnemon1k.dwitter.excaptions.ApiException;
import com.mnemon1k.dwitter.shared.GenericResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/1.0")
public class UserController {

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    Page<UserDTO> getUsers(){
        return userService.getAllUsers().map(UserDTO::new);
    }

    @PostMapping("/users")
    GenericResponse createUser(@RequestBody @Valid User user){
        userService.save(user);
        return new GenericResponse("User saved");
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
