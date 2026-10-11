package com.example.SOA_GIUAKY.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "SINHVIEN")
public class SinhVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_sinh_vien", nullable = false, unique = true, length = 30)
    private String maSinhVien;

    @Column(name = "ho_ten", nullable = false, length = 120)
    private String hoTen;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String nganh;

    protected SinhVien() {}

    public SinhVien(String maSinhVien, String hoTen, String email, String nganh) {
        this.maSinhVien = maSinhVien;
        this.hoTen = hoTen;
        this.email = email;
        this.nganh = nganh;
    }

    public Long getId() { return id; }
    public String getMaSinhVien() { return maSinhVien; }
    public void setMaSinhVien(String maSinhVien) { this.maSinhVien = maSinhVien; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNganh() { return nganh; }
    public void setNganh(String nganh) { this.nganh = nganh; }
}