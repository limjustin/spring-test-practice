package dev.limjustin.testdev.pay.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayRepository extends JpaRepository<Pay, Long> {
    List<Pay> findByUser_Id(Long userId);
}
