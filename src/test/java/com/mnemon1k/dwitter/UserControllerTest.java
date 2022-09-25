package com.mnemon1k.dwitter;

import com.mnemon1k.dwitter.User.User;
import com.mnemon1k.dwitter.User.UserRepository;
import com.mnemon1k.dwitter.excaptions.ApiException;
import com.mnemon1k.dwitter.shared.GenericResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.mnemon1k.dwitter.TestUtil.createUser;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest {
    public static final String API_1_0_USERS = "/api/1.0/users";

    TestRestTemplate restTemplate;
    UserRepository userRepository;

    @Autowired
    public UserControllerTest(TestRestTemplate restTemplate, UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    public <T> ResponseEntity<T> postSignup(Object request, Class<T> response){
        return restTemplate.postForEntity(API_1_0_USERS, request, response);
    }

    private String generateStringOfLength(int strLength){
        return IntStream.rangeClosed(1,strLength).mapToObj(x -> "a").collect(Collectors.joining());
    }

    @Test
    public void postUser_whenUserIsValid_receiveOk(){
        User user = createUser();
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postUser_whenUserIsValid_userSavedToDatabase(){
        User user = createUser();
        postSignup(user, Object.class);
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    public void postUser_whenUserIsValid_passwordIsHashedInDatabase(){
        User user = createUser();
        postSignup(user, Object.class);
        User userFromDb = userRepository.findAll().get(0);
        assertThat(userFromDb.getPassword()).isNotEqualTo(user.getPassword());
    }

    @Test
    public void postUser_whenUserIsValid_receiveSuccessMessage(){
        User user = createUser();
        ResponseEntity<GenericResponse> response = postSignup(user, GenericResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isNotNull();
    }

    @Test
    public void postUser_whenUserHasNullUsername_receiveBadRequest(){
        User user = createUser();
        user.setUsername(null);

        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasUsernameWithLessThanRequired_receiveBadRequest(){
        User user = createUser();
        user.setUsername("qwe");

        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasUsernameMoreThanRequired_receiveBadRequest(){
        User user = createUser();
        String valueOf33Char = generateStringOfLength(33);
        user.setUsername(valueOf33Char);

        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasDisplayNameWithLessThanRequired_receiveBadRequest(){
        User user = createUser();
        user.setDisplayName("qwe");

        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasDisplayNameMoreThanRequired_receiveBadRequest(){
        User user = createUser();
        String valueOf129Char = generateStringOfLength(129);
        user.setDisplayName(valueOf129Char);

        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordWithLessThanRequired_receiveBadRequest(){
        User user = createUser();
        user.setPassword("Qwe1");

        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordMoreThanRequired_receiveBadRequest(){
        User user = createUser();
        String valueOf65Char = generateStringOfLength(65);
        user.setPassword(valueOf65Char);

        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordWithAllLowercaseLetters_receiveBadRequest(){
        User user = createUser();
        user.setPassword("lowwercasepassword");

        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordWithAllUppercaseLetters_receiveBadRequest(){
        User user = createUser();
        user.setPassword("UPPERCASEPASSWORD");

        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordWithAllNumbers_receiveBadRequest(){
        User user = createUser();
        user.setPassword("132456789456");

        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullDisplayName_receiveBadRequest(){
        User user = createUser();
        user.setDisplayName(null);

        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullPassword_receiveBadRequest(){
        User user = createUser();
        user.setPassword(null);

        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserIsInvalid_receiveApiException(){
        User user = new User();

        ResponseEntity<ApiException> response = postSignup(user, ApiException.class);
        assertThat(Objects.requireNonNull(response.getBody()).getUrl()).isEqualTo(API_1_0_USERS);
    }

    @Test
    public void postUser_whenUserIsInvalid_receiveApiExceptionWithErrorFields(){
        User user = new User();

        ResponseEntity<ApiException> response = postSignup(user, ApiException.class);
        assertThat(Objects.requireNonNull(response.getBody()).getValidationErrors().size()).isEqualTo(3);
    }

    @Test
    public void postUser_whenAnotherUserHasSameUsername_receiveBadRequest(){
        userRepository.save(createUser());
        User user = createUser();
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenAnotherUserHasSameUsername_receiveMessageDuplicateUsername(){
        userRepository.save(createUser());

        User user = createUser();
        ResponseEntity<ApiException> response = postSignup(user, ApiException.class);
        Map<String, String> validationErrors = Objects.requireNonNull(response.getBody()).getValidationErrors();
        assertThat(validationErrors.get("username")).isEqualTo("This username is in use.");
    }

    @Test
    public void getUsers_whenThereAreNoUsersInDB_receiveOk (){
        ResponseEntity<Object> response = getUsers(new ParameterizedTypeReference<Object>() {        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getUsers_whenThereAreNoUsersInDB_receivePageWithZeroItems (){
        ResponseEntity<TestPage<Object>> response = restTemplate.exchange(API_1_0_USERS, HttpMethod.GET, null, new ParameterizedTypeReference<TestPage<Object>>() {});

        assertThat(Objects.requireNonNull(response.getBody()).getTotalElements()).isEqualTo(0);
    }

    @Test
    public void getUsers_whenThereIsUserInDB_receivePageWithUser (){
        userRepository.save(createUser());
        ResponseEntity<TestPage<Object>> response = restTemplate.exchange(API_1_0_USERS, HttpMethod.GET, null, new ParameterizedTypeReference<TestPage<Object>>() {});

        assertThat(Objects.requireNonNull(response.getBody()).getNumberOfElements()).isEqualTo(1);
    }

    @Test
    public void getUsers_whenThereIsUserInDB_receiveUserWithoutPassword (){
        userRepository.save(createUser());
        ResponseEntity<TestPage<Map<String, Object>>> response = restTemplate.exchange(API_1_0_USERS, HttpMethod.GET, null, new ParameterizedTypeReference<TestPage<Map<String, Object>>>() {});

        assertThat(Objects.requireNonNull(response.getBody()).getContent().get(0).containsKey("password")).isFalse();

    }

    @Test
    public void getUsers_whenPageIsRequestedFor3ItemsPerPageWhereDBHHas20Users_receive3Users(){
        IntStream.rangeClosed(1, 20).mapToObj(i -> "test-user-" + i)
                .map(TestUtil::createUser)
                .forEach(userRepository::save);

        String path = API_1_0_USERS + "?page=0&size=3";

        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>() {
        });

        assertThat(Objects.requireNonNull(response.getBody()).getContent().size()).isEqualTo(3);
    }

    @Test
    public void getUsers_whenPageNotProvided_receivePageSizeAs10(){
        ResponseEntity<TestPage<Object>> response = getUsers(new ParameterizedTypeReference<TestPage<Object>>() {});

        assertThat(Objects.requireNonNull(response.getBody()).getSize()).isEqualTo(10);
    }

    @Test
    public void getUsers_whenPageSizeMoreThan100_receivePageSizeAs100(){
        String path = API_1_0_USERS + "?size=500";

        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>() {});

        assertThat(Objects.requireNonNull(response.getBody()).getSize()).isEqualTo(100);
    }

    @Test
    public void getUsers_whenPageSizeIsNegative_receivePageSizeAs10(){
        String path = API_1_0_USERS + "?size=-50";

        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>() {});

        assertThat(Objects.requireNonNull(response.getBody()).getSize()).isEqualTo(10);
    }

    public <T> ResponseEntity<T> getUsers(ParameterizedTypeReference<T> responseType){
        return restTemplate.exchange(API_1_0_USERS, HttpMethod.GET, null, responseType);
    }

    public <T> ResponseEntity<T> getUsers(String path, ParameterizedTypeReference<T> responseType){
        return restTemplate.exchange(path, HttpMethod.GET, null, responseType);
    }
}
