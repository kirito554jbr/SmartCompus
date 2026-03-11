package org.example.smartcompus.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartcompus.dto.AbsenceDto.AbsenceDto;
import org.example.smartcompus.service.interfaces.IAbsenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/absences")
@RequiredArgsConstructor
public class AbsenceController {

    private final IAbsenceService absenceService;

    @PostMapping
    public ResponseEntity<AbsenceDto> markAbsent(@Valid @RequestBody AbsenceDto dto) {
        return new ResponseEntity<>(absenceService.markStudentAbsent(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AbsenceDto>> getAllAbsences() {
        return ResponseEntity.ok(absenceService.getAllAbsences());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AbsenceDto> getAbsenceById(@PathVariable Long id) {
        return ResponseEntity.ok(absenceService.getAbsenceById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AbsenceDto>> getAbsencesByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(absenceService.getAbsencesByStudent(studentId));
    }

    @PatchMapping("/{id}/justify")
    public ResponseEntity<AbsenceDto> justifyAbsence(
            @PathVariable Long id,
            @RequestParam boolean accepted) {
        return ResponseEntity.ok(absenceService.justifyAbsence(id, accepted));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbsence(@PathVariable Long id) {
        absenceService.deleteAbsence(id);
        return ResponseEntity.noContent().build();
    }
}

