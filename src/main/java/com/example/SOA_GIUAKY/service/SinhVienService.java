package com.example.SOA_GIUAKY.service;

import com.example.SOA_GIUAKY.api.ApiModels.SinhVienRequest;
import com.example.SOA_GIUAKY.api.ApiModels.SinhVienResponse;
import com.example.SOA_GIUAKY.entity.SinhVien;
import com.example.SOA_GIUAKY.repository.DangKyRepository;
import com.example.SOA_GIUAKY.repository.SinhVienRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class SinhVienService {
    private final SinhVienRepository repository;
    private final DangKyRepository dangKyRepository;

    public SinhVienService(SinhVienRepository repository, DangKyRepository dangKyRepository) {
        this.repository = repository;
        this.dangKyRepository = dangKyRepository;
    }

    @Transactional(readOnly = true)
    public List<SinhVienResponse> findAll() { return repository.findAll().stream().map(this::toResponse).toList(); }

    @Transactional(readOnly = true)
    public SinhVienResponse findById(Long id) { return toResponse(requireStudent(id)); }

    public SinhVienResponse create(SinhVienRequest request) {
        validate(request);
        if (repository.existsByMaSinhVien(request.maSinhVien().trim()) || repository.existsByEmail(request.email().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã sinh viên hoặc email đã được sử dụng");
        }
        return toResponse(repository.save(new SinhVien(request.maSinhVien().trim(), request.hoTen().trim(),
                request.email().trim(), request.nganh().trim())));
    }

    public SinhVienResponse update(Long id, SinhVienRequest request) {
        validate(request);
        SinhVien student = requireStudent(id);
        boolean duplicateCode = repository.existsByMaSinhVienAndIdNot(request.maSinhVien().trim(), id);
        boolean duplicateEmail = repository.existsByEmailAndIdNot(request.email().trim(), id);
        if (duplicateCode || duplicateEmail) throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã sinh viên hoặc email đã được sử dụng");
        student.setMaSinhVien(request.maSinhVien().trim());
        student.setHoTen(request.hoTen().trim());
        student.setEmail(request.email().trim());
        student.setNganh(request.nganh().trim());
        return toResponse(repository.save(student));
    }

    public void delete(Long id) {
        requireStudent(id);
        if (dangKyRepository.existsBySinhVienId(id)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Không thể xóa sinh viên đang có đăng ký đồ án");
        repository.deleteById(id);
    }

    private SinhVien requireStudent(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên"));
    }

    private void validate(SinhVienRequest request) {
        if (request == null || blank(request.maSinhVien()) || blank(request.hoTen()) || blank(request.email()) || blank(request.nganh())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Các trường mã sinh viên, họ tên, email và ngành là bắt buộc");
        }
    }

    private boolean blank(String value) { return value == null || value.isBlank(); }
    private SinhVienResponse toResponse(SinhVien student) {
        return new SinhVienResponse(student.getId(), student.getMaSinhVien(), student.getHoTen(), student.getEmail(), student.getNganh());
    }
}