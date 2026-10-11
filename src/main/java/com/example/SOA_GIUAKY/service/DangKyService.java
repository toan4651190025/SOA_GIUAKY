package com.example.SOA_GIUAKY.service;

import com.example.SOA_GIUAKY.api.ApiModels.DangKyRequest;
import com.example.SOA_GIUAKY.api.ApiModels.DangKyResponse;
import com.example.SOA_GIUAKY.entity.DangKy;
import com.example.SOA_GIUAKY.entity.DeTai;
import com.example.SOA_GIUAKY.entity.SinhVien;
import com.example.SOA_GIUAKY.repository.DangKyRepository;
import com.example.SOA_GIUAKY.repository.DeTaiRepository;
import com.example.SOA_GIUAKY.repository.SinhVienRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class DangKyService {
    private final DangKyRepository repository;
    private final SinhVienRepository sinhVienRepository;
    private final DeTaiRepository deTaiRepository;

    public DangKyService(DangKyRepository repository, SinhVienRepository sinhVienRepository, DeTaiRepository deTaiRepository) {
        this.repository = repository;
        this.sinhVienRepository = sinhVienRepository;
        this.deTaiRepository = deTaiRepository;
    }

    @Transactional(readOnly = true)
    public List<DangKyResponse> findAll() { return repository.findAll().stream().map(this::toResponse).toList(); }

    @Transactional(readOnly = true)
    public DangKyResponse findById(Long id) { return toResponse(requireRegistration(id)); }

    public DangKyResponse create(DangKyRequest request) {
        validate(request);
        SinhVien student = requireStudent(request.sinhVienId());
        DeTai topic = requireTopic(request.deTaiId());
        if (repository.existsBySinhVienIdAndDeTaiId(student.getId(), topic.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sinh viên đã đăng ký đề tài này");
        }
        if (repository.countByDeTaiId(topic.getId()) >= topic.getChiTieu()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Đề tài đã đủ chỉ tiêu");
        }
        DangKy registration = new DangKy(student, topic, status(request.trangThai()));
        applyScores(registration, request);
        return toResponse(repository.save(registration));
    }

    public DangKyResponse update(Long id, DangKyRequest request) {
        validate(request);
        DangKy registration = requireRegistration(id);
        SinhVien student = requireStudent(request.sinhVienId());
        DeTai topic = requireTopic(request.deTaiId());
        if (repository.existsBySinhVienIdAndDeTaiId(student.getId(), topic.getId())
            && !(registration.getSinhVien().getId().equals(student.getId()) && registration.getDeTai().getId().equals(topic.getId()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sinh viên đã đăng ký đề tài này");
        }
        if (!registration.getDeTai().getId().equals(topic.getId()) && repository.countByDeTaiId(topic.getId()) >= topic.getChiTieu()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Đề tài đã đủ chỉ tiêu");
        }
        registration.setSinhVien(student);
        registration.setDeTai(topic);
        registration.setTrangThai(status(request.trangThai()));
        applyScores(registration, request);
        return toResponse(repository.save(registration));
    }

    public void delete(Long id) { repository.delete(requireRegistration(id)); }

    private DangKy requireRegistration(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đăng ký"));
    }

    private SinhVien requireStudent(Long id) {
        if (id == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sinhVienId là bắt buộc");
        return sinhVienRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên"));
    }

    private DeTai requireTopic(Long id) {
        if (id == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "deTaiId là bắt buộc");
        return deTaiRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đề tài"));
    }

    private void validate(DangKyRequest request) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dữ liệu đăng ký không được để trống");
        validateScore(request.diemHuongDan());
        validateScore(request.diemPhanBien());
        validateScore(request.diemHoiDong());
    }

    private void validateScore(Double score) {
        if (score != null && (!Double.isFinite(score) || score < 0 || score > 10)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Điểm phải nằm trong khoảng từ 0 đến 10");
        }
    }

    private String status(String value) { return value == null || value.isBlank() ? "DANG_KY" : value.trim().toUpperCase(); }

    private void applyScores(DangKy registration, DangKyRequest request) {
        registration.setDiemHuongDan(request.diemHuongDan());
        registration.setDiemPhanBien(request.diemPhanBien());
        registration.setDiemHoiDong(request.diemHoiDong());
    }

    private DangKyResponse toResponse(DangKy registration) {
        Double score = null;
        String result = "CHUA_CHAM";
        if (registration.getDiemHuongDan() != null && registration.getDiemPhanBien() != null && registration.getDiemHoiDong() != null) {
            score = BigDecimal.valueOf(registration.getDiemHuongDan() * 0.3 + registration.getDiemPhanBien() * 0.3
                    + registration.getDiemHoiDong() * 0.4).setScale(2, RoundingMode.HALF_UP).doubleValue();
            result = score >= 5 ? "DAT" : "KHONG_DAT";
        }
        return new DangKyResponse(registration.getId(), registration.getSinhVien().getId(), registration.getSinhVien().getMaSinhVien(),
                registration.getSinhVien().getHoTen(), registration.getDeTai().getId(), registration.getDeTai().getTenDeTai(),
                registration.getTrangThai(), registration.getDiemHuongDan(), registration.getDiemPhanBien(),
                registration.getDiemHoiDong(), score, result);
    }
}