package com.mnemon1k.dwitter.Record;

import com.mnemon1k.dwitter.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RecordService {

    RecordRepository recordRepository;

    @Autowired
    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public void save(Record record, User user){
        record.setTimestamp(new Date());
        record.setUser(user);
        List<Record> records = user.getRecords();

        if (records == null){
            records = new ArrayList<>();
            records.add(record);
        }

        user.setRecords(records);
        recordRepository.save(record);
    }
}
