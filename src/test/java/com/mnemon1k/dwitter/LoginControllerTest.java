package com.mnemon1k.dwitter;


import com.mnemon1k.dwitter.User.User;
import com.mnemon1k.dwitter.User.UserRepository;
import com.mnemon1k.dwitter.User.UserService;
import com.mnemon1k.dwitter.excaptions.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Objects;

import static com.mnemon1k.dwitter.TestUtil.createUser;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LoginControllerTest {
    public static final String API_1_0_LOGIN = "/api/1.0/login";

    TestRestTemplate restTemplate;
    UserRepository userRepository;
    UserService userService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        restTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Autowired
    public LoginControllerTest(TestRestTemplate restTemplate, UserRepository userRepository, UserService userService) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Test
    public void postLogin_withoutUserCredentials_receiveUnauthorized(){
        ResponseEntity<Object> response = login(Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void postLogin_withoutUserCredentials_receiveApiError(){
        ResponseEntity<ApiException> response = login(ApiException.class);
        assertThat(Objects.requireNonNull(response.getBody()).getUrl()).isEqualTo(API_1_0_LOGIN);
    }

    @Test
    public void postLogin_withoutUserCredentials_receiveApiErrorWithoutValidationErrors(){
        ResponseEntity<String> response = login(String.class);
        assertThat(Objects.requireNonNull(response.getBody())
                .contains("validationErrors")).isFalse();
    }

    @Test
    public void postLogin_withIncorrectUserCredentials_receiveUnauthorized(){
        authenticate();
        ResponseEntity<Object> response = login(Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void postLogin_withIncorrectUserCredentials_receiveUnauthorizedWithoutWWWAuthenticationHeader(){
        authenticate();
        ResponseEntity<Object> response = login(Object.class);
        assertThat(response.getHeaders().containsKey("WWW-Authenticate")).isFalse();
    }

    @Test
    public void postLogin_withValidCredentials_receiveOk(){
        userService.save(createUser());
        authenticate();
        ResponseEntity<Object> response = login(Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postLogin_withValidCredentials_receiveLoggedInUserId(){
        User userFromDb = userService.save(createUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<>() {
        });

        Map<String, Object> body = response.getBody();

        assert body != null;
        Integer id = (Integer) body.get("id");
        assertThat(id).isEqualTo(userFromDb.getId());
    }

    @Test
    public void postLogin_withValidCredentials_receiveLoggedInUserImage(){
        User userFromDb = userService.save(createUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<>() {
        });

        Map<String, Object> body = response.getBody();

        assert body != null;
        String image = (String) body.get("image");
        assertThat(image).isEqualTo(userFromDb.getImage());
    }

    @Test
    public void postLogin_withValidCredentials_receiveLoggedInUserDisplayName(){
        User userFromDb = userService.save(createUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<>() {
        });

        Map<String, Object> body = response.getBody();

        assert body != null;
        String image = (String) body.get("displayName");
        assertThat(image).isEqualTo(userFromDb.getDisplayName());
    }

    @Test
    public void postLogin_withValidCredentials_receiveLoggedInUserUsername(){
        User userFromDb = userService.save(createUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<>() {
        });

        Map<String, Object> body = response.getBody();

        assert body != null;
        String image = (String) body.get("username");
        assertThat(image).isEqualTo(userFromDb.getUsername());
    }

    @Test
    public void postLogin_withValidCredentials_receiveLoggedInUserWithoutPassword(){
        userService.save(createUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<>() {
        });

        Map<String, Object> body = response.getBody();

        assert body != null;
        assertThat(body.containsKey("password")).isFalse();
    }

    private void authenticate() {
        restTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor("alex", "AlexPass123"));
    }

    public <T> ResponseEntity<T> login(Class<T> responseType){
        return restTemplate.postForEntity(API_1_0_LOGIN, null, responseType);
    }

    public <T> ResponseEntity<T> login(ParameterizedTypeReference<T> responseType){
        return restTemplate.exchange(API_1_0_LOGIN, HttpMethod.POST, null, responseType);
    }
}
