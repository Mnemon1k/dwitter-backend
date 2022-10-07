package com.mnemon1k.dwitter.Record;

import com.mnemon1k.dwitter.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {
    Page<Record> findByUser(User user, Pageable pageable);
    Page<Record> findByIdLessThan(long id, Pageable pageable);
    Page<Record> findByUserAndIdLessThan(User user, long id, Pageable pageable);
}
