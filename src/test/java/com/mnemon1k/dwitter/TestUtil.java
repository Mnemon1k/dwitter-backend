package com.mnemon1k.dwitter;

import com.mnemon1k.dwitter.Record.Record;
import com.mnemon1k.dwitter.User.User;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public static Record createRecord(){
        Record record = new Record();
        record.setContent("Test record");
        return record;
    }

    public static String generateStringOfLength(int strLength){
        return IntStream.rangeClosed(1,strLength).mapToObj(x -> "a").collect(Collectors.joining());
    }
}
