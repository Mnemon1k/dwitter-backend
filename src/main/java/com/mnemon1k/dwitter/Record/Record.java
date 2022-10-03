package com.mnemon1k.dwitter.Record;

import com.mnemon1k.dwitter.User.User;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@Entity
public class Record {
    @Id
    @GeneratedValue
    private long id;

    @NotNull
    @Size(min = 10, max = 333)
    @Column(length = 333)
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private User user;
}
