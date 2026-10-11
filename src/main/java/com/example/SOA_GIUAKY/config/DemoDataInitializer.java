package com.example.SOA_GIUAKY.config;

import com.example.SOA_GIUAKY.entity.DangKy;
import com.example.SOA_GIUAKY.entity.DeTai;
import com.example.SOA_GIUAKY.entity.SinhVien;
import com.example.SOA_GIUAKY.repository.DangKyRepository;
import com.example.SOA_GIUAKY.repository.DeTaiRepository;
import com.example.SOA_GIUAKY.repository.SinhVienRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDataInitializer {
    @Bean
    CommandLineRunner seedDemoData(SinhVienRepository sinhVienRepository, DeTaiRepository deTaiRepository,
                                   DangKyRepository dangKyRepository) {
        return args -> {
            if (sinhVienRepository.count() > 0 || deTaiRepository.count() > 0 || dangKyRepository.count() > 0) return;

            SinhVien sv1 = sinhVienRepository.save(new SinhVien("SV2021001", "Nguyen Minh Anh", "minhanh@university.edu.vn", "Cong nghe phan mem"));
            SinhVien sv2 = sinhVienRepository.save(new SinhVien("SV2021002", "Tran Quoc Bao", "quocbao@university.edu.vn", "He thong thong tin"));
            SinhVien sv3 = sinhVienRepository.save(new SinhVien("SV2021003", "Le Thu Ha", "thuha@university.edu.vn", "An toan thong tin"));

            DeTai dt1 = deTaiRepository.save(new DeTai("DT001", "Nen tang quan ly do an tot nghiep", "ThS. Pham Thu Trang", 4,
                    "Xay dung REST API va giao dien quan tri do an."));
            DeTai dt2 = deTaiRepository.save(new DeTai("DT002", "Ung dung nhan dien diem danh", "TS. Nguyen Van Long", 3,
                    "Thu nghiem mo hinh thi giac may tinh trong lop hoc."));
            DeTai dt3 = deTaiRepository.save(new DeTai("DT003", "He thong thu thap log tap trung", "ThS. Do Hoang Nam", 2,
                    "Phan tich log va canh bao su co he thong."));

            DangKy dk1 = new DangKy(sv1, dt1, "DANG_THUC_HIEN");
            dk1.setDiemHuongDan(8.5);
            dk1.setDiemPhanBien(8.0);
            dk1.setDiemHoiDong(9.0);
            dangKyRepository.save(dk1);
            dangKyRepository.save(new DangKy(sv2, dt2, "DANG_KY"));
            dangKyRepository.save(new DangKy(sv3, dt3, "DANG_KY"));
        };
    }
}