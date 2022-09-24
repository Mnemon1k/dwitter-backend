package com.mnemon1k.dwitter.User;

import com.mnemon1k.dwitter.User.DTO.UserDTO;
import com.mnemon1k.dwitter.shared.CurrentUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @PostMapping("/api/1.0/login")
    public UserDTO handleLogin(@CurrentUser User loggedInUser){
        return new UserDTO(loggedInUser);
    }

}
