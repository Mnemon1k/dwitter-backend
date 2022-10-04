package com.mnemon1k.dwitter.Record;

import com.mnemon1k.dwitter.User.DTO.UserDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecordDTO {
    private long id;
    private String content;
    private long date;
    private UserDTO user;

    public RecordDTO(Record record){
        this.setId(record.getId());
        this.setContent(record.getContent());
        this.setDate(record.getTimestamp().getTime());
        this.setUser(new UserDTO(record.getUser()));
    }
}
