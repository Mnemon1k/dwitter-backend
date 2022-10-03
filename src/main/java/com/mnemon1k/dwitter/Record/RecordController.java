package com.mnemon1k.dwitter.Record;

import com.mnemon1k.dwitter.User.User;
import com.mnemon1k.dwitter.shared.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/1.0/records")
public class RecordController {

    RecordService recordService;

    @Autowired
    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    void createRecord(@RequestBody @Valid Record record, @CurrentUser User user){
        recordService.save(record, user);
    }
}
