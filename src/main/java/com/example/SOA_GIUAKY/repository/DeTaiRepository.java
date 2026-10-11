package com.example.SOA_GIUAKY.repository;

import com.example.SOA_GIUAKY.entity.DeTai;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeTaiRepository extends JpaRepository<DeTai, Long> {
    boolean existsByMaDeTai(String maDeTai);
    boolean existsByMaDeTaiAndIdNot(String maDeTai, Long id);
}