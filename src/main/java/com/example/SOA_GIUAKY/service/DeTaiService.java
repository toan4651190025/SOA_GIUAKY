package com.example.SOA_GIUAKY.service;

import com.example.SOA_GIUAKY.api.ApiModels.DeTaiRequest;
import com.example.SOA_GIUAKY.api.ApiModels.DeTaiResponse;
import com.example.SOA_GIUAKY.entity.DeTai;
import com.example.SOA_GIUAKY.repository.DangKyRepository;
import com.example.SOA_GIUAKY.repository.DeTaiRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class DeTaiService {
    private final DeTaiRepository repository;
    private final DangKyRepository dangKyRepository;

    public DeTaiService(DeTaiRepository repository, DangKyRepository dangKyRepository) {
        this.repository = repository;
        this.dangKyRepository = dangKyRepository;
    }

    @Transactional(readOnly = true)
    public List<DeTaiResponse> findAll() { return repository.findAll().stream().map(this::toResponse).toList(); }

    @Transactional(readOnly = true)
    public DeTaiResponse findById(Long id) { return toResponse(requireTopic(id)); }

    public DeTaiResponse create(DeTaiRequest request) {
        validate(request);
        if (repository.existsByMaDeTai(request.maDeTai().trim())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã đề tài đã được sử dụng");
        return toResponse(repository.save(new DeTai(request.maDeTai().trim(), request.tenDeTai().trim(),
                request.giangVienHuongDan().trim(), request.chiTieu(), clean(request.moTa()))));
    }

    public DeTaiResponse update(Long id, DeTaiRequest request) {
        validate(request);
        DeTai topic = requireTopic(id);
        boolean duplicate = repository.existsByMaDeTaiAndIdNot(request.maDeTai().trim(), id);
        if (duplicate) throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã đề tài đã được sử dụng");
        if (request.chiTieu() < dangKyRepository.countByDeTaiId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Chỉ tiêu không thể thấp hơn số sinh viên đã đăng ký");
        }
        topic.setMaDeTai(request.maDeTai().trim());
        topic.setTenDeTai(request.tenDeTai().trim());
        topic.setGiangVienHuongDan(request.giangVienHuongDan().trim());
        topic.setChiTieu(request.chiTieu());
        topic.setMoTa(clean(request.moTa()));
        return toResponse(repository.save(topic));
    }

    public void delete(Long id) {
        requireTopic(id);
        if (dangKyRepository.existsByDeTaiId(id)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Không thể xóa đề tài đang có sinh viên đăng ký");
        repository.deleteById(id);
    }

    private DeTai requireTopic(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đề tài"));
    }

    private void validate(DeTaiRequest request) {
        if (request == null || blank(request.maDeTai()) || blank(request.tenDeTai()) || blank(request.giangVienHuongDan())
                || request.chiTieu() == null || request.chiTieu() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã, tên, giảng viên và chỉ tiêu (tối thiểu 1) là bắt buộc");
        }
    }

    private boolean blank(String value) { return value == null || value.isBlank(); }
    private String clean(String value) { return value == null ? null : value.trim(); }
    private DeTaiResponse toResponse(DeTai topic) {
        return new DeTaiResponse(topic.getId(), topic.getMaDeTai(), topic.getTenDeTai(), topic.getGiangVienHuongDan(), topic.getChiTieu(), topic.getMoTa());
    }
}