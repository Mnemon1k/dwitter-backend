package com.mnemon1k.dwitter;

import com.mnemon1k.dwitter.User.User;

public class TestUtil {

    public static User createUser() {
        User user = new User();
        user.setUsername("alex");
        user.setDisplayName("alex-name");
        user.setPassword("AlexPass123");
        user.setImage("profile-image.png");
        return user;
    }

    public static User createUser(String username) {
        User user = createUser();
        user.setUsername(username);
        return user;
    }
}
