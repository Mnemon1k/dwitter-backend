package com.mnemon1k.dwitter;

import com.mnemon1k.dwitter.User.User;
import com.mnemon1k.dwitter.User.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.mnemon1k.dwitter.TestUtil.createUser;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    UserRepository userRepository;

    @Test
    public void findByUsername_whenUserExists_returnsUser(){
        User user = createUser();
        testEntityManager.persist(user);

        Optional<User> userFromDb = userRepository.findByUsername(user.getUsername());

        assertThat(userFromDb.isPresent()).isTrue();
    }

    @Test
    public void findByUsername_whenUserDoesNotExist_returnsNull(){
        Optional<User> userFromDb = userRepository.findByUsername("username");

        assertThat(userFromDb.isPresent()).isFalse();
    }

}
