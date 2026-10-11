package com.example.SOA_GIUAKY.repository;

import com.example.SOA_GIUAKY.entity.DangKy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DangKyRepository extends JpaRepository<DangKy, Long> {
    boolean existsBySinhVienIdAndDeTaiId(Long sinhVienId, Long deTaiId);
    long countByDeTaiId(Long deTaiId);
    boolean existsBySinhVienId(Long sinhVienId);
    boolean existsByDeTaiId(Long deTaiId);
}