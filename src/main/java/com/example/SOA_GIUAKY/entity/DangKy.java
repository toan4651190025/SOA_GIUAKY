package com.example.SOA_GIUAKY.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "DANGKY", uniqueConstraints = @UniqueConstraint(columnNames = {"sinh_vien_id", "de_tai_id"}))
public class DangKy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sinh_vien_id", nullable = false)
    private SinhVien sinhVien;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "de_tai_id", nullable = false)
    private DeTai deTai;

    @Column(nullable = false, length = 30)
    private String trangThai;

    private Double diemHuongDan;
    private Double diemPhanBien;
    private Double diemHoiDong;

    protected DangKy() {}

    public DangKy(SinhVien sinhVien, DeTai deTai, String trangThai) {
        this.sinhVien = sinhVien;
        this.deTai = deTai;
        this.trangThai = trangThai;
    }

    public Long getId() { return id; }
    public SinhVien getSinhVien() { return sinhVien; }
    public void setSinhVien(SinhVien sinhVien) { this.sinhVien = sinhVien; }
    public DeTai getDeTai() { return deTai; }
    public void setDeTai(DeTai deTai) { this.deTai = deTai; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public Double getDiemHuongDan() { return diemHuongDan; }
    public void setDiemHuongDan(Double diemHuongDan) { this.diemHuongDan = diemHuongDan; }
    public Double getDiemPhanBien() { return diemPhanBien; }
    public void setDiemPhanBien(Double diemPhanBien) { this.diemPhanBien = diemPhanBien; }
    public Double getDiemHoiDong() { return diemHoiDong; }
    public void setDiemHoiDong(Double diemHoiDong) { this.diemHoiDong = diemHoiDong; }
}