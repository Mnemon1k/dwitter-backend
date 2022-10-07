package com.mnemon1k.dwitter;

import com.mnemon1k.dwitter.Record.Record;
import com.mnemon1k.dwitter.Record.RecordDTO;
import com.mnemon1k.dwitter.Record.RecordRepository;
import com.mnemon1k.dwitter.Record.RecordService;
import com.mnemon1k.dwitter.User.User;
import com.mnemon1k.dwitter.User.UserRepository;
import com.mnemon1k.dwitter.User.UserService;
import com.mnemon1k.dwitter.excaptions.ApiException;
import org.junit.jupiter.api.AfterEach;
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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.Objects;

import static com.mnemon1k.dwitter.TestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RecordControllerTest {
    private final String API_RECORDS_PATH = "/api/1.0/records";

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UserService userService;

    @Autowired
    RecordService recordService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RecordRepository recordRepository;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        recordRepository.deleteAll();
        restTemplate.getRestTemplate().getInterceptors().clear();
    }

    private void authenticate(String username) {
        restTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor(username, "AlexPass123"));
    }

    private <T> ResponseEntity<T> postRecord(Record record, Class<T> type) {
        return restTemplate.postForEntity(API_RECORDS_PATH, record, type);
    }

    private <T> ResponseEntity<T> getRecords(ParameterizedTypeReference<T> responseType) {
        return restTemplate.exchange(API_RECORDS_PATH, HttpMethod.GET, null, responseType);
    }

    private <T> ResponseEntity<T> getRecordsOfUser(String username, ParameterizedTypeReference<T> responseType) {
        return restTemplate.exchange("/api/1.0/users/"+username+"/records", HttpMethod.GET, null, responseType);
    }

    private <T> ResponseEntity<T> getPrevRecords(long id, ParameterizedTypeReference<T> responseType) {
        return restTemplate.exchange(API_RECORDS_PATH + "/" + id + "?direction=before&page=0&size=5&sort=id,desc", HttpMethod.GET, null, responseType);
    }

    private <T> ResponseEntity<T> getPrevRecordsOfUser(long id, String username, ParameterizedTypeReference<T> responseType) {
        return restTemplate.exchange("/api/1.0/users/"+username+"/records/" + id + "?direction=before&page=0&size=5&sort=id,desc", HttpMethod.GET, null, responseType);
    }

    @Test
    public void postRecord_whenRecordIsValidAndUserIsAuthorized_receiveOk(){
        userService.save(TestUtil.createUser("user1"));
        authenticate("user1");
        Record record = createRecord();
        ResponseEntity<Object> response = postRecord(record, Object.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postRecord_whenRecordIsValidAndUserIsUnauthorized_receiveUnauthorized(){
        Record record = createRecord();
        ResponseEntity<Object> response = postRecord(record, Object.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void postRecord_whenRecordIsValidAndUserIsUnauthorized_receiveApiException(){
        Record record = createRecord();
        ResponseEntity<ApiException> response = postRecord(record, ApiException.class);

        assertThat(Objects.requireNonNull(response.getBody()).getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void postRecord_whenRecordIsValidAndUserIsAuthorized_RecordSavedToDb(){
        userService.save(TestUtil.createUser("user1"));
        authenticate("user1");
        Record record = createRecord();
        postRecord(record, Object.class);

        assertThat(recordRepository.count()).isEqualTo(1);
    }

    @Test
    public void postRecord_whenRecordIsValidAndUserIsAuthorized_RecordSavedToDbWithTimeStamp(){
        userService.save(TestUtil.createUser("user1"));
        authenticate("user1");
        Record record = createRecord();
        postRecord(record, Object.class);

        Record recordFromDb = recordRepository.findAll().get(0);

        assertThat(recordFromDb.getTimestamp())
                .isNotNull();
    }

    @Test
    public void postRecord_whenRecordContentIsNullAndUserIsAuthorized_receiveBadRequest(){
        userService.save(TestUtil.createUser("user1"));
        authenticate("user1");
        Record record = new Record();
        ResponseEntity<Object> response = postRecord(record, Object.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postRecord_whenRecordContentIsLessThan10CharsAndUserIsAuthorized_receiveBadRequest(){
        userService.save(TestUtil.createUser("user1"));
        authenticate("user1");
        Record record = new Record();
        record.setContent("123345");
        ResponseEntity<Object> response = postRecord(record, Object.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postRecord_whenRecordContentIsMoreThan333CharsAndUserIsAuthorized_receiveBadRequest(){
        userService.save(TestUtil.createUser("user1"));
        authenticate("user1");
        Record record = new Record();
        record.setContent(generateStringOfLength(350));
        ResponseEntity<Object> response = postRecord(record, Object.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postRecord_whenRecordContentIsMoreThan10AndLessThan333CharsAndUserIsAuthorized_receiveOk(){
        userService.save(TestUtil.createUser("user1"));
        authenticate("user1");
        Record record = new Record();
        record.setContent(generateStringOfLength(100));
        ResponseEntity<Object> response = postRecord(record, Object.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postRecord_whenRecordContentIsNullAndUserIsAuthorized_receiveApiExceptionWithValidationErrors(){
        userService.save(TestUtil.createUser("user1"));
        authenticate("user1");
        Record record = new Record();
        ResponseEntity<ApiException> response = postRecord(record, ApiException.class);

        assertThat(Objects.requireNonNull(response.getBody()).getValidationErrors().get("content"))
                .isNotNull();
    }

    @Test
    public void postRecord_whenRecordIsValidAndUserIsAuthorized_recordSavedWithAuthUserInfo(){
        userService.save(TestUtil.createUser("user1"));
        authenticate("user1");
        Record record = createRecord();
        postRecord(record, Object.class);

        Record recordFromDb = recordRepository.findAll().get(0);

        assertThat(recordFromDb.getUser().getUsername()).isEqualTo("user1");
    }

    @Test
    public void postRecord_whenRecordIsValidAndUserIsAuthorized_userHaveRecordsList(){
        User user1 = userService.save(TestUtil.createUser("user1"));
        authenticate("user1");
        Record record = createRecord();
        postRecord(record, Object.class);

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User userFromDb = entityManager.find(User.class, user1.getId());

        assertThat(userFromDb.getRecords().size()).isEqualTo(1);
    }

    @Test
    public void getRecords_whenThereAreNoRecords_receiveOk(){
        ResponseEntity<Object> response = getRecords(new ParameterizedTypeReference<>() {});
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getRecords_whenThereAreNoRecords_receivePageWithZeroItems(){
        ResponseEntity<TestPage<Object>> response = getRecords(new ParameterizedTypeReference<>() {});
        assertThat(Objects.requireNonNull(response.getBody()).getTotalElements())
                .isEqualTo(0);
    }

    @Test
    public void getRecords_whenThereAreRecords_receivePageWithItems(){
        User user = userService.save(TestUtil.createUser("user1"));
        recordService.save(createRecord(), user);
        recordService.save(createRecord(), user);
        recordService.save(createRecord(), user);

        ResponseEntity<TestPage<Object>> response = getRecords(new ParameterizedTypeReference<>() {});
        assertThat(Objects.requireNonNull(response.getBody()).getTotalElements())
                .isEqualTo(3);
    }

    @Test
    public void getRecords_whenThereAreRecords_receivePageWithRecordDTO(){
        User user = userService.save(TestUtil.createUser("user1"));
        recordService.save(createRecord(), user);

        ResponseEntity<TestPage<RecordDTO>> response = getRecords(new ParameterizedTypeReference<>() {
        });

        assertThat(
                Objects.requireNonNull(response.getBody()).getContent().get(0)
                        .getUser().getUsername()
        )
                .isEqualTo("user1");
    }

    @Test
    public void postRecord_whenRecordIsValidAndUserIsAuthorized_receiveRecordDTO(){
        userService.save(TestUtil.createUser("user1"));
        authenticate("user1");
        Record record = createRecord();
        ResponseEntity<RecordDTO> response = postRecord(record, RecordDTO.class);

        assertThat(Objects.requireNonNull(response.getBody()).getUser().getUsername())
                .isEqualTo("user1");
    }

    @Test
    public void getRecordsOfUser_whenUserExists_receiveOk(){
        userService.save(TestUtil.createUser("user1"));
        ResponseEntity<Object> response = getRecordsOfUser("user1", new ParameterizedTypeReference<>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getRecordsOfUser_whenUserNotExists_receiveNotFound(){
        ResponseEntity<Object> response = getRecordsOfUser("randomUser", new ParameterizedTypeReference<>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getRecordsOfUser_whenUserExists_receivePageWithZeroRecords(){
        ResponseEntity<TestPage<Record>> response = getRecordsOfUser("user1", new ParameterizedTypeReference<>() {
        });
        assertThat(Objects.requireNonNull(response.getBody()).getTotalElements())
                .isEqualTo(0);
    }

    @Test
    public void getRecordsOfUser_whenUserExistsWithRecords_receivePageOfRecordDTO(){
        User user = userService.save(TestUtil.createUser("user1"));
        recordService.save(createRecord(), user);
        ResponseEntity<TestPage<RecordDTO>> response = getRecordsOfUser("user1", new ParameterizedTypeReference<>(){});

        assertThat(Objects.requireNonNull(response.getBody()).getContent().get(0).getUser().getUsername())
                .isEqualTo("user1");
    }

    @Test
    public void getRecordsOfUser_whenUserExistsWithMultipleRecords_receivePageWithMatchingRecordsCount(){
        User user = userService.save(TestUtil.createUser("user1"));
        recordService.save(createRecord(), user);
        recordService.save(createRecord(), user);
        recordService.save(createRecord(), user);

        ResponseEntity<TestPage<RecordDTO>> response = getRecordsOfUser("user1", new ParameterizedTypeReference<>(){});

        assertThat(Objects.requireNonNull(response.getBody()).getTotalElements())
                .isEqualTo(3);
    }

    @Test
    public void getPrevRecords_whenThereAreNoRecords_receiveOk(){
        ResponseEntity<Object> response = getPrevRecords(5, new ParameterizedTypeReference<>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getPrevRecords_whenThereAreRecords_receivePageWithItemsBeforeProvidedId(){
        User user = userService.save(createUser("user1"));
        recordService.save(createRecord(), user);
        recordService.save(createRecord(), user);
        recordService.save(createRecord(), user);
        Record record = recordService.save(createRecord(), user);
        recordService.save(createRecord(), user);

        ResponseEntity<TestPage<Object>> response = getPrevRecords(record.getId(), new ParameterizedTypeReference<>() {});

        assertThat(Objects.requireNonNull(response.getBody()).getTotalElements())
                .isEqualTo(3);
    }

    @Test
    public void getPrevRecords_whenThereAreRecords_receivePageWithRecordDTOBeforeProvidedId(){
        User user = userService.save(createUser("user1"));
        recordService.save(createRecord(), user);
        recordService.save(createRecord(), user);
        recordService.save(createRecord(), user);
        Record record = recordService.save(createRecord(), user);
        recordService.save(createRecord(), user);

        ResponseEntity<TestPage<RecordDTO>> response = getPrevRecords(record.getId(), new ParameterizedTypeReference<>() {});

        assertThat(Objects.requireNonNull(response.getBody()).getContent().get(0).getDate())
                .isGreaterThan(0);
    }

    @Test
    public void getPrevRecordsOfUser_whenUserExistsAndThereAreNoRecords_receiveOk(){
        User user = userService.save(createUser("user1"));
        ResponseEntity<Object> response = getPrevRecordsOfUser(5, user.getUsername(), new ParameterizedTypeReference<>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getPrevRecordsOfUser_whenUserExistsThereAreRecords_receivePageWithItemsBeforeProvidedId(){
        User user = userService.save(createUser("user1"));
        recordService.save(createRecord(), user);
        recordService.save(createRecord(), user);
        recordService.save(createRecord(), user);
        Record record = recordService.save(createRecord(), user);
        recordService.save(createRecord(), user);

        ResponseEntity<TestPage<Object>> response = getPrevRecordsOfUser(record.getId(), user.getUsername(), new ParameterizedTypeReference<>() {});

        assertThat(Objects.requireNonNull(response.getBody()).getTotalElements())
                .isEqualTo(3);
    }

    @Test
    public void getPrevRecordsOfUser_whenUserExistsThereAreNoRecords_receivePageWithZeroItemsBeforeProvidedId(){
        User user1 = userService.save(createUser("user1"));
        User user2 = userService.save(createUser("user2"));
        recordService.save(createRecord(), user1);
        recordService.save(createRecord(), user1);
        recordService.save(createRecord(), user1);
        Record record = recordService.save(createRecord(), user1);
        recordService.save(createRecord(), user1);

        ResponseEntity<TestPage<Object>> response = getPrevRecordsOfUser(record.getId(), user2.getUsername(), new ParameterizedTypeReference<>() {});

        assertThat(Objects.requireNonNull(response.getBody()).getTotalElements())
                .isEqualTo(0);
    }

    @Test
    public void getPrevRecordsOfUser_whenUserNotExistsAndThereAreNoRecords_receiveNotFound(){
        ResponseEntity<Object> response = getPrevRecordsOfUser(5, "user1", new ParameterizedTypeReference<>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @AfterEach
    public void cleanupAfter() {
        recordRepository.deleteAll();
    }
}
