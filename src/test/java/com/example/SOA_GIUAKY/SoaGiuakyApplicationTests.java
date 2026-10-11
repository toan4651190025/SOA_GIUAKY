package com.example.SOA_GIUAKY;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:soa-test;DB_CLOSE_DELAY=-1")
class SoaGiuakyApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
	}

	@Test
	void createsRegistrationAndCalculatesWeightedScore() throws Exception {
		MvcResult studentResult = mockMvc.perform(post("/api/sinhvien")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"maSinhVien":"SVTEST01","hoTen":"Test Student","email":"test01@university.edu.vn","nganh":"CNTT"}
						"""))
				.andExpect(status().isCreated())
				.andReturn();
		long studentId = objectMapper.readTree(studentResult.getResponse().getContentAsString()).get("id").asLong();

		MvcResult topicResult = mockMvc.perform(post("/api/detai")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"maDeTai":"DTTEST01","tenDeTai":"Test topic","giangVienHuongDan":"Test advisor","chiTieu":1,"moTa":"Test"}
						"""))
				.andExpect(status().isCreated())
				.andReturn();
		long topicId = objectMapper.readTree(topicResult.getResponse().getContentAsString()).get("id").asLong();

		mockMvc.perform(post("/api/dangky")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"sinhVienId":%d,"deTaiId":%d,"trangThai":"DA_HOAN_THANH","diemHuongDan":8,"diemPhanBien":9,"diemHoiDong":7}
						""".formatted(studentId, topicId)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.diemTongKet").value(7.9))
				.andExpect(jsonPath("$.ketQua").value("DAT"));
	}

}
