package org.example.smartcompus.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartcompus.dto.MajorDto.MajorDto;
import org.example.smartcompus.service.interfaces.IMajorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/major")
public class MajorController {
    private final IMajorService majorService;

    @PostMapping
    public ResponseEntity<MajorDto> createMajor(@Valid @RequestBody MajorDto majorDto) {
        return new ResponseEntity<>(majorService.createMajor(majorDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MajorDto>> getMajors() {
        return ResponseEntity.ok(majorService.getMajors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MajorDto> getMajorById(@PathVariable Long id) {
        return ResponseEntity.ok(majorService.getMajorById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MajorDto> updateMajor(@PathVariable Long id, @Valid @RequestBody MajorDto majorDto) {
        return ResponseEntity.ok(majorService.updateMajor(majorDto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMajor(@PathVariable Long id) {
        majorService.deleteMajor(id);
        return ResponseEntity.noContent().build();
    }
}
