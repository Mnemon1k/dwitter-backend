package com.mnemon1k.dwitter.Record;

import com.mnemon1k.dwitter.User.User;
import com.mnemon1k.dwitter.shared.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/1.0")
public class RecordController {

    RecordService recordService;

    @Autowired
    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping("/records")
    Page<RecordDTO> geAllRecords(Pageable pageable){
        return recordService.getAllRecords(pageable).map(RecordDTO::new);
    }

    @PostMapping("/records")
    RecordDTO createRecord(@RequestBody @Valid Record record, @CurrentUser User user){
        Record recordFromDb = recordService.save(record, user);
        return new RecordDTO(recordFromDb);
    }

    @GetMapping("/users/{username}/records")
    Page<RecordDTO> getUserRecords(@PathVariable String username, Pageable pageable) {
        return recordService.getRecordsOfUserByUsername(username, pageable).map(RecordDTO::new);
    }

    @GetMapping("/records/{id:[0-9]+}")
    Page<RecordDTO> getRecordsRelative(@PathVariable long id, Pageable pageable){
        return recordService.getPrevRecords(id, pageable).map(RecordDTO::new);
    }

    @GetMapping("/users/{username}/records/{id:[0-9]+}")
    Page<RecordDTO> getRecordRelativeOfUser(@PathVariable long id, @PathVariable String username, Pageable pageable){
        return recordService.getPrevRecordsByUser(id, username, pageable).map(RecordDTO::new);
    }

}
