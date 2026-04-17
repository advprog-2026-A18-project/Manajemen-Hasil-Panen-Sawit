package id.ac.ui.cs.advprog.sawitpanen.controller;

import id.ac.ui.cs.advprog.sawitpanen.dto.CreatePanenRequest;
import id.ac.ui.cs.advprog.sawitpanen.dto.PanenResponse;
import id.ac.ui.cs.advprog.sawitpanen.model.Panen;
import id.ac.ui.cs.advprog.sawitpanen.service.PanenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/panen")
public class PanenController {
    @Autowired
    private PanenService panenService;

    @PostMapping
    public ResponseEntity<PanenResponse> createLaporanPanen(
            @RequestBody CreatePanenRequest request) {
        PanenResponse laporanBaru = panenService.createLaporan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(laporanBaru);
    }

    @GetMapping
    public ResponseEntity<List<PanenResponse>> getAllPanen() {
        return ResponseEntity.ok(panenService.getAllPanen());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PanenResponse> getPanenDetail(@PathVariable UUID id) {
        return ResponseEntity.ok(panenService.getPanenById(id));
    }
}
