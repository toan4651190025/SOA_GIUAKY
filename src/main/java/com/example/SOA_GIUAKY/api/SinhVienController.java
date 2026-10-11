package com.example.SOA_GIUAKY.api;

import com.example.SOA_GIUAKY.api.ApiModels.SinhVienRequest;
import com.example.SOA_GIUAKY.api.ApiModels.SinhVienResponse;
import com.example.SOA_GIUAKY.service.SinhVienService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sinhvien")
public class SinhVienController {
    private final SinhVienService service;
    public SinhVienController(SinhVienService service) { this.service = service; }

    @GetMapping public List<SinhVienResponse> findAll() { return service.findAll(); }
    @GetMapping("/{id}") public SinhVienResponse findById(@PathVariable Long id) { return service.findById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public SinhVienResponse create(@RequestBody SinhVienRequest request) { return service.create(request); }
    @PutMapping("/{id}") public SinhVienResponse update(@PathVariable Long id, @RequestBody SinhVienRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.delete(id); }
}