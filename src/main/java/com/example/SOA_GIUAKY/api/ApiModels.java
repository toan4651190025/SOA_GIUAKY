package com.example.SOA_GIUAKY.api;

public final class ApiModels {
    private ApiModels() {}

    public record SinhVienRequest(String maSinhVien, String hoTen, String email, String nganh) {}
    public record SinhVienResponse(Long id, String maSinhVien, String hoTen, String email, String nganh) {}
    public record DeTaiRequest(String maDeTai, String tenDeTai, String giangVienHuongDan, Integer chiTieu, String moTa) {}
    public record DeTaiResponse(Long id, String maDeTai, String tenDeTai, String giangVienHuongDan, Integer chiTieu, String moTa) {}
    public record DangKyRequest(Long sinhVienId, Long deTaiId, String trangThai, Double diemHuongDan,
                                Double diemPhanBien, Double diemHoiDong) {}
    public record DangKyResponse(Long id, Long sinhVienId, String maSinhVien, String tenSinhVien,
                                 Long deTaiId, String tenDeTai, String trangThai, Double diemHuongDan,
                                 Double diemPhanBien, Double diemHoiDong, Double diemTongKet, String ketQua) {}
}