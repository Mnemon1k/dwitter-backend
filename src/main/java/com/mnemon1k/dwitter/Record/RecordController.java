package com.mnemon1k.dwitter.Record;

import com.mnemon1k.dwitter.User.User;
import com.mnemon1k.dwitter.shared.CurrentUser;
import com.mnemon1k.dwitter.shared.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @GetMapping({"/records/{id:[0-9]+}", "/users/{username}/records/{id:[0-9]+}"})
    ResponseEntity<?> getRecordsRelative(
            @PathVariable long id,
            @PathVariable(required = false) String username,
            Pageable pageable,
            @RequestParam(name = "direction", defaultValue = "after") String direction,
            @RequestParam(name = "count", defaultValue = "false", required = false) boolean count
    ){
        if (direction.equalsIgnoreCase("before")){
            return ResponseEntity.ok(recordService.getPrevRecords(id, username, pageable).map(RecordDTO::new));
        }

        if (count){
            long countValueFromDb = recordService.getRecordsCount(id, username);
            return ResponseEntity.ok(Collections.singletonMap("count", countValueFromDb));
        }

        return ResponseEntity.ok(recordService.getNextRecords(id, username, pageable).stream().map(RecordDTO::new).collect(Collectors.toList()));
    }

    @DeleteMapping("/records/{id:[0-9]+}")
    GenericResponse deleteRecord(
            @PathVariable long id,
            @CurrentUser User user
    ){
        Optional<Record> recordById = recordService.getRecordById(id);
        if (recordById.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post #" + id + " not found");

        if (Objects.equals(recordById.get().getUser().getUsername(), user.getUsername())) {
            recordService.deleteRecord(id);
            return new GenericResponse("Record removed");
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }
}
