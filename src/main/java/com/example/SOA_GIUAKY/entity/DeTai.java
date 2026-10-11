package com.example.SOA_GIUAKY.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "DETAI")
public class DeTai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_de_tai", nullable = false, unique = true, length = 30)
    private String maDeTai;

    @Column(nullable = false, length = 180)
    private String tenDeTai;

    @Column(nullable = false, length = 120)
    private String giangVienHuongDan;

    @Column(nullable = false)
    private Integer chiTieu;

    @Column(length = 500)
    private String moTa;

    protected DeTai() {}

    public DeTai(String maDeTai, String tenDeTai, String giangVienHuongDan, Integer chiTieu, String moTa) {
        this.maDeTai = maDeTai;
        this.tenDeTai = tenDeTai;
        this.giangVienHuongDan = giangVienHuongDan;
        this.chiTieu = chiTieu;
        this.moTa = moTa;
    }

    public Long getId() { return id; }
    public String getMaDeTai() { return maDeTai; }
    public void setMaDeTai(String maDeTai) { this.maDeTai = maDeTai; }
    public String getTenDeTai() { return tenDeTai; }
    public void setTenDeTai(String tenDeTai) { this.tenDeTai = tenDeTai; }
    public String getGiangVienHuongDan() { return giangVienHuongDan; }
    public void setGiangVienHuongDan(String giangVienHuongDan) { this.giangVienHuongDan = giangVienHuongDan; }
    public Integer getChiTieu() { return chiTieu; }
    public void setChiTieu(Integer chiTieu) { this.chiTieu = chiTieu; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}