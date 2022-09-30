package com.mnemon1k.dwitter;

import com.mnemon1k.dwitter.User.DTO.UserDTO;
import com.mnemon1k.dwitter.User.DTO.UserUpdateDTO;
import com.mnemon1k.dwitter.User.User;
import com.mnemon1k.dwitter.User.UserRepository;
import com.mnemon1k.dwitter.User.UserService;
import com.mnemon1k.dwitter.configuration.AppConfiguration;
import com.mnemon1k.dwitter.excaptions.ApiException;
import com.mnemon1k.dwitter.shared.GenericResponse;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    UserService userService;
    AppConfiguration appConfig;

    @Autowired
    public UserControllerTest(TestRestTemplate restTemplate, UserRepository userRepository, UserService userService, AppConfiguration appConfig) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.userService = userService;
        this.appConfig = appConfig;
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        restTemplate.getRestTemplate().getInterceptors().clear();
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
        user.setPassword("lowercase-password");

        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordWithAllUppercaseLetters_receiveBadRequest(){
        User user = createUser();
        user.setPassword("UPPERCASE-PASSWORD");

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
        ResponseEntity<Object> response = getUsers(new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getUsers_whenThereAreNoUsersInDB_receivePageWithZeroItems (){
        ResponseEntity<TestPage<Object>> response = restTemplate.exchange(API_1_0_USERS, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });

        assertThat(Objects.requireNonNull(response.getBody()).getTotalElements()).isEqualTo(0);
    }

    @Test
    public void getUsers_whenThereIsUserInDB_receivePageWithUser (){
        userRepository.save(createUser());
        ResponseEntity<TestPage<Object>> response = restTemplate.exchange(API_1_0_USERS, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });

        assertThat(Objects.requireNonNull(response.getBody()).getNumberOfElements()).isEqualTo(1);
    }

    @Test
    public void getUsers_whenThereIsUserInDB_receiveUserWithoutPassword (){
        userRepository.save(createUser());
        ResponseEntity<TestPage<Map<String, Object>>> response = restTemplate.exchange(API_1_0_USERS, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });

        assertThat(Objects.requireNonNull(response.getBody()).getContent().get(0).containsKey("password")).isFalse();

    }

    @Test
    public void getUsers_whenPageIsRequestedFor3ItemsPerPageWhereDBHHas20Users_receive3Users(){
        IntStream.rangeClosed(1, 20).mapToObj(i -> "test-user-" + i)
                .map(TestUtil::createUser)
                .forEach(userRepository::save);

        String path = API_1_0_USERS + "?page=0&size=3";

        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<>() {
        });

        assertThat(Objects.requireNonNull(response.getBody()).getContent().size()).isEqualTo(3);
    }

    @Test
    public void getUsers_whenPageNotProvided_receivePageSizeAs10(){
        ResponseEntity<TestPage<Object>> response = getUsers(new ParameterizedTypeReference<>() {
        });

        assertThat(Objects.requireNonNull(response.getBody()).getSize()).isEqualTo(10);
    }

    @Test
    public void getUsers_whenPageSizeMoreThan100_receivePageSizeAs100(){
        String path = API_1_0_USERS + "?size=500";

        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<>() {
        });

        System.out.println(response.getBody());

        assertThat(Objects.requireNonNull(response.getBody()).getSize())
                .isEqualTo(100);
    }

    @Test
    public void getUsers_whenPageSizeIsNegative_receivePageSizeAs10(){
        String path = API_1_0_USERS + "?size=-50";

        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<>() {
        });

        assertThat(Objects.requireNonNull(response.getBody()).getSize()).isEqualTo(10);
    }

    @Test
    public void getUsers_whenUserLoggedIn_receivePageWithoutLoggedInUser(){
        userService.save(TestUtil.createUser("user1"));
        userService.save(TestUtil.createUser("user2"));
        userService.save(TestUtil.createUser("user3"));

        authenticate("user1");

        ResponseEntity<TestPage<Object>> response = getUsers(new ParameterizedTypeReference<>() {
        });

        assertThat(Objects.requireNonNull(response.getBody()).getTotalElements()).isEqualTo(2);
    }

    @Test
    public void getUserByUsername_whenUserExists_receiveOk(){
        String username = "test-user";
        userService.save(createUser(username));
        ResponseEntity<Object> response = getUser(username, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getUserByUsername_whenUserExists_receiveUserWithoutPassword(){
        String username = "test-user";
        userService.save(createUser(username));
        ResponseEntity<String> response = getUser(username, String.class);
        assertThat(Objects.requireNonNull(response.getBody()).contains("password"))
                .isFalse();
    }

    @Test
    public void getUserByUsername_whenUserDoesNotExists_receiveNotFound (){
        String username = "unknown-user";
        ResponseEntity<Object> response = getUser(username, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getUserByUsername_whenUserDoesNotExists_receiveApiException (){
        String username = "unknown-user";
        ResponseEntity<ApiException> response = getUser(username, ApiException.class);
        assertThat(
                Objects.requireNonNull(response.getBody())
                        .getMessage()
                        .contains("unknown-user")
        )
                .isTrue();
    }

    @Test
    public void putUser_whenUnauthorizedUserSandRequest_receiveUnauthorized(){
        ResponseEntity<Object> response = putUser(123, null, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void putUser_whenAuthorizedUserUpdateAnotherUser_receiveForbidden(){
        User user = userService.save(createUser("user-1"));
        authenticate(user.getUsername());
        long anotherUserId = user.getId() + 121;
        ResponseEntity<Object> response = putUser(anotherUserId, null, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void putUser_whenUnauthorizedUserSandRequest_receiveApiException(){
        ResponseEntity<ApiException> response = putUser(123, null, ApiException.class);

        assertThat(Objects.requireNonNull(response.getBody()).getUrl())
                .contains("users/123");
    }

    @Test
    public void putUser_whenAuthorizedUserUpdateAnotherUser_receiveApiException(){
        User user = userService.save(createUser("user-1"));
        authenticate(user.getUsername());
        long anotherUserId = user.getId() + 121;
        ResponseEntity<ApiException> response = putUser(anotherUserId, null, ApiException.class);
        assertThat(Objects.requireNonNull(response.getBody()).getUrl())
                .contains("users/" + anotherUserId);
    }

    @Test
    public void putUser_whenValidUserIdFromAuthorizedUser_receiveOk(){
        User user = userService.save(createUser("user-1"));
        authenticate(user.getUsername());
        UserUpdateDTO updateUser = getUserUpdateDTO();
        HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(updateUser);
        ResponseEntity<Object> response = putUser(user.getId(), httpEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void putUser_whenValidUserIdFromAuthorizedUser_displayNameUpdated(){
        User user = userService.save(createUser("user-1"));
        authenticate(user.getUsername());
        UserUpdateDTO updateUser = getUserUpdateDTO();
        HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(updateUser);
        putUser(user.getId(), httpEntity, Object.class);

        Optional<User> userFromDb = userRepository.findByUsername(user.getUsername());

        assertThat(userFromDb.get().getDisplayName())
                .isEqualTo(updateUser.getDisplayName());
    }

    @Test
    public void putUser_whenValidUserIdFromAuthorizedUser_receiveUserDTOWithUpdatedDisplayName(){
        User user = userService.save(createUser("user-1"));
        authenticate(user.getUsername());
        UserUpdateDTO updateUser = getUserUpdateDTO();
        HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(updateUser);
        ResponseEntity<UserDTO> response = putUser(user.getId(), httpEntity, UserDTO.class);

        assertThat(Objects.requireNonNull(response.getBody()).getDisplayName())
                .isEqualTo(updateUser.getDisplayName());
    }

    @Test
    public void putUser_withValidRequestBodyFromAuthorizedUser_receiveUserVMWithRandomImageName() throws IOException {
        User user = userService.save(createUser("user-1"));
        authenticate(user.getUsername());
        UserUpdateDTO updateUser = getUserUpdateDTO();
        String imageString = readFileToBase64("profile.png");
        updateUser.setImage(imageString);

        HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(updateUser);
        ResponseEntity<UserDTO> response = putUser(user.getId(), httpEntity, UserDTO.class);

        assertThat(Objects.requireNonNull(response.getBody()).getImage())
                .isNotEqualTo("profile-image.png");
    }

    @Test
    public void putUser_withValidRequestBodyFromAuthorizedUser_imageSavedInProfileFolder() throws IOException {
        User user = userService.save(createUser("user-1"));
        authenticate(user.getUsername());
        UserUpdateDTO updateUser = getUserUpdateDTO();
        String imageString = readFileToBase64("profile.png");
        updateUser.setImage(imageString);

        HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(updateUser);
        ResponseEntity<UserDTO> response = putUser(user.getId(), httpEntity, UserDTO.class);

        String savedImageName = Objects.requireNonNull(response.getBody()).getImage();

        String profileImagePath = appConfig.getFullProfileImagesPath() + "/" + savedImageName;
        File savedImage = new File(profileImagePath);

        assertThat(savedImage.exists()).isTrue();
    }

    @Test
    public void putUser_withInvalidRequestBodyWithNullDisplayNameFromAuthorizedUser_receiveBadRequest(){
        User user = userService.save(createUser("user-1"));
        authenticate(user.getUsername());
        UserUpdateDTO updateUser = new UserUpdateDTO();

        HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(updateUser);
        ResponseEntity<Object> response = putUser(user.getId(), httpEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void putUser_withInvalidRequestBodyWithMoreThanMaxSizeDisplayNameFromAuthorizedUser_receiveBadRequest(){
        User user = userService.save(createUser("user-1"));
        authenticate(user.getUsername());
        UserUpdateDTO updateUser = new UserUpdateDTO();
        updateUser.setDisplayName(IntStream.rangeClosed(1,300).mapToObj(x -> "a").collect(Collectors.joining()));

        HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(updateUser);
        ResponseEntity<Object> response = putUser(user.getId(), httpEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void putUser_withInvalidRequestBodyWithJPGImageFromAuthorizedUser_receiveOk() throws IOException {
        User user = userService.save(createUser("user-1"));
        authenticate(user.getUsername());
        UserUpdateDTO updateUser = getUserUpdateDTO();
        String imageString = readFileToBase64("test-jpg.jpg");
        updateUser.setImage(imageString);

        HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(updateUser);
        ResponseEntity<UserDTO> response = putUser(user.getId(), httpEntity, UserDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void putUser_withInvalidRequestBodyWithGIFImageFromAuthorizedUser_receiveBadRequest() throws IOException {
        User user = userService.save(createUser("user-1"));
        authenticate(user.getUsername());
        UserUpdateDTO updateUser = getUserUpdateDTO();
        String imageString = readFileToBase64("test-gif.gif");
        updateUser.setImage(imageString);

        HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(updateUser);
        ResponseEntity<Object> response = putUser(user.getId(), httpEntity, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void putUser_withInvalidRequestBodyWithTXTFileForImageFieldFromAuthorizedUser_receiveValidationErrorForProfileImage() throws IOException {
        User user = userService.save(createUser("user-1"));
        authenticate(user.getUsername());
        UserUpdateDTO updateUser = getUserUpdateDTO();
        String imageString = readFileToBase64("test-txt.txt");
        updateUser.setImage(imageString);

        HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(updateUser);
        ResponseEntity<ApiException> response = putUser(user.getId(), httpEntity, ApiException.class);
        Map<String, String> validationErrors = Objects.requireNonNull(response.getBody()).getValidationErrors();

        assertThat(validationErrors.get("image")).isEqualTo("Only PNG and JPG file are allowed");
    }



    private String readFileToBase64(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        byte[] imageArr = FileUtils.readFileToByteArray(resource.getFile());
        return Base64.getEncoder().encodeToString(imageArr);
    }

    private static UserUpdateDTO getUserUpdateDTO() {
        UserUpdateDTO updateUser = new UserUpdateDTO();
        updateUser.setDisplayName("newDisplayName");
        return updateUser;
    }

    private void authenticate(String username) {
        restTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor(username, "AlexPass123"));
    }

    public <T> ResponseEntity<T> getUsers(ParameterizedTypeReference<T> responseType){
        return restTemplate.exchange(API_1_0_USERS, HttpMethod.GET, null, responseType);
    }

    public <T> ResponseEntity<T> getUsers(String path, ParameterizedTypeReference<T> responseType){
        return restTemplate.exchange(path, HttpMethod.GET, null, responseType);
    }

    public <T> ResponseEntity<T> getUser(String username, Class<T> responseType){
        String path = API_1_0_USERS + "/" + username;
        return restTemplate.getForEntity(path, responseType);
    }

    public <T> ResponseEntity<T> putUser(long id, HttpEntity<?> requestEntity, Class<T> responseType){
        String path = API_1_0_USERS + "/" + id;
        return restTemplate.exchange(path, HttpMethod.PUT, requestEntity, responseType);
    }

    @AfterEach
    public void cleanup() throws IOException {
        FileUtils.cleanDirectory(new File(appConfig.getFullProfileImagesPath()));
        FileUtils.cleanDirectory(new File(appConfig.getFullAttachmentsPath()));
    }
}
