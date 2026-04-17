package id.ac.ui.cs.advprog.sawitpanen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import id.ac.ui.cs.advprog.sawitpanen.dto.ApprovalRequest;
import id.ac.ui.cs.advprog.sawitpanen.dto.CreatePanenRequest;
import id.ac.ui.cs.advprog.sawitpanen.dto.PanenResponse;
import id.ac.ui.cs.advprog.sawitpanen.model.StatusPanen;
import id.ac.ui.cs.advprog.sawitpanen.service.PanenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PanenController.class)
class PanenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @MockitoBean
    private PanenService panenService;

    private UUID dummyBuruhId;
    private UUID dummyPanenId;
    private PanenResponse dummyResponse;

    @BeforeEach
    void setUp() {
        dummyBuruhId = UUID.randomUUID();
        dummyPanenId = UUID.randomUUID();

        dummyResponse = new PanenResponse();
        dummyResponse.setId(dummyPanenId);
        dummyResponse.setBuruhId(dummyBuruhId);
        dummyResponse.setKuantitasBerat(100);
        dummyResponse.setBerita("Panen lancar");
        dummyResponse.setBuktiFoto(List.of("http://foto.com/1.jpg"));
        dummyResponse.setTanggalPanen(LocalDate.now());
        dummyResponse.setStatus(StatusPanen.REPORTED);
    }

    @Test
    void createLaporanPanen_Sukses_Return201() throws Exception {
        CreatePanenRequest request = new CreatePanenRequest(
                dummyBuruhId,
                100,
                "Panen lancar",
                List.of("http://foto.com/1.jpg"));

        when(panenService.createLaporan(any(CreatePanenRequest.class))).thenReturn(dummyResponse);

        mockMvc.perform(post("/api/panen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(dummyPanenId.toString()))
                .andExpect(jsonPath("$.status").value("REPORTED"));
    }

    @Test
    void createLaporanPanen_GagalValidasi_Return400() throws Exception {
        CreatePanenRequest invalidRequest = new CreatePanenRequest(
                dummyBuruhId,
                0,
                "Panen lancar",
                List.of("http://foto.com/1.jpg"));

        mockMvc.perform(post("/api/panen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPanen_Sukses_ReturnPagedData() throws Exception {
        Page<PanenResponse> pagedResponse = new PageImpl<>(List.of(dummyResponse));

        when(panenService.getPanenByFilter(
                eq(dummyBuruhId), any(), any(), any(), eq(StatusPanen.REPORTED), any(Pageable.class)))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/panen")
                        .param("buruh_id", dummyBuruhId.toString())
                        .param("status", "REPORTED")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(dummyPanenId.toString()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getPanenDetail_Sukses_Return200() throws Exception {
        when(panenService.getPanenById(dummyPanenId)).thenReturn(dummyResponse);

        mockMvc.perform(get("/api/panen/{id}", dummyPanenId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dummyPanenId.toString()));
    }

    @Test
    void processApproval_Sukses_Return200() throws Exception {
        UUID mandorId = UUID.randomUUID();
        ApprovalRequest approvalReq = new ApprovalRequest();
        approvalReq.setStatus(StatusPanen.APPROVED);

        dummyResponse.setStatus(StatusPanen.APPROVED);
        dummyResponse.setMandorId(mandorId);

        when(panenService.processApproval(eq(dummyPanenId), eq(mandorId), any(ApprovalRequest.class)))
                .thenReturn(dummyResponse);

        mockMvc.perform(patch("/api/panen/{id}/approval", dummyPanenId)
                        .header("X-User-Id", mandorId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approvalReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.mandorId").value(mandorId.toString()));
    }
}