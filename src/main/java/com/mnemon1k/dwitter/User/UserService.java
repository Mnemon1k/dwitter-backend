package com.mnemon1k.dwitter.User;

import com.mnemon1k.dwitter.File.FileService;
import com.mnemon1k.dwitter.User.DTO.UserUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;

@Service
public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    FileService fileService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, FileService fileService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileService = fileService;
    }

    public User save(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, username + " Not Found");
        }
        return user.get();
    }

    public Page<User> getAllUsers(User loggedInUser, Pageable pageable) {
        if (loggedInUser != null){
            return userRepository.findByUsernameNot(loggedInUser.getUsername(), pageable);
        }

        return userRepository.findAll(pageable);
    }

    public User update(long id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.getReferenceById(id);
        user.setDisplayName(userUpdateDTO.getDisplayName());

        if (userUpdateDTO.getImage() != null){
            try {
                String imageName = fileService.saveProfileImage(userUpdateDTO.getImage());
                fileService.deleteProfileImage(user.getImage());
                user.setImage(imageName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }



        return  userRepository.save(user);
    }
}
