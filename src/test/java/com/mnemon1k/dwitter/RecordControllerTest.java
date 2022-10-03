package com.mnemon1k.dwitter;

import com.mnemon1k.dwitter.Record.Record;
import com.mnemon1k.dwitter.Record.RecordRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.Objects;

import static com.mnemon1k.dwitter.TestUtil.createRecord;
import static com.mnemon1k.dwitter.TestUtil.generateStringOfLength;
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

    @AfterEach
    public void cleanupAfter() {
        recordRepository.deleteAll();
    }
}
