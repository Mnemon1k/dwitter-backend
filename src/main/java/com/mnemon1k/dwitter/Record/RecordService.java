package com.mnemon1k.dwitter.Record;

import com.mnemon1k.dwitter.User.User;
import com.mnemon1k.dwitter.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RecordService {
    RecordRepository recordRepository;

    UserService userService;

    @Autowired
    public RecordService(RecordRepository recordRepository, UserService userService) {
        this.recordRepository = recordRepository;
        this.userService = userService;
    }

    public Record save(Record record, User user){
        record.setTimestamp(new Date());
        record.setUser(user);
//        List<Record> records = user.getRecords();
//
//        if (records == null){
//            records = new ArrayList<>();
//            records.add(record);
//        }
//
//        user.setRecords(records);
        return recordRepository.save(record);
    }
    public Page<Record> getAllRecords(Pageable pageable) {
        return recordRepository.findAll(pageable);
    }
    public Page<Record> getRecordsOfUserByUsername(String username, Pageable pageable) {
        User user = userService.getUserByUsername(username);
        return recordRepository.findByUser(user, pageable);
    }
    public Page<Record> getPrevRecords(long id, String username, Pageable pageable){
        Specification<Record> spec = Specification.where(idLessThan(id));

        if (username != null)
            spec = spec.and(userIs(userService.getUserByUsername(username)));

        return recordRepository.findAll(spec, pageable);
    }

    public List<Record> getNextRecords(long id, String username, Pageable pageable){
        Specification<Record> spec = Specification.where(idGreaterThan(id));

        if (username != null)
            spec = spec.and(userIs(userService.getUserByUsername(username)));

        return recordRepository.findAll(spec, pageable.getSort());
    }
    public long getRecordsCount(long id, String username) {
        Specification<Record> spec = Specification.where(idGreaterThan(id));

        if (username != null)
            spec = spec.and(userIs(userService.getUserByUsername(username)));

        return recordRepository.count(spec);
    }

    private Specification<Record> userIs(User user){
        return (root, query, criteriaBuilder)->{
            return criteriaBuilder.equal(root.get("user"), user);
        };
    }
    private Specification<Record> idLessThan(long id){
        return (root, query, criteriaBuilder)->{
            return criteriaBuilder.lessThan(root.get("id"), id);
        };
    }
    private Specification<Record> idGreaterThan(long id){
        return (root, query, criteriaBuilder)->{
            return criteriaBuilder.greaterThan(root.get("id"), id);
        };
    }
}
