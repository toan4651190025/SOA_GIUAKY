package com.example.SOA_GIUAKY.repository;

import com.example.SOA_GIUAKY.entity.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SinhVienRepository extends JpaRepository<SinhVien, Long> {
    boolean existsByMaSinhVien(String maSinhVien);
    boolean existsByEmail(String email);
    boolean existsByMaSinhVienAndIdNot(String maSinhVien, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
}