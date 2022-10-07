package com.mnemon1k.dwitter.Record;

import com.mnemon1k.dwitter.User.User;
import com.mnemon1k.dwitter.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

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

    public Page<Record> getPrevRecords(long id, Pageable pageable){
        return recordRepository.findByIdLessThan(id, pageable);
    }

    public Page<Record> getPrevRecordsByUser(long id, String username, Pageable pageable){
        User user = userService.getUserByUsername(username);
        return recordRepository.findByUserAndIdLessThan(user, id, pageable);
    }
}
