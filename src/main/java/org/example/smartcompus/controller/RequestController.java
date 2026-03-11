package org.example.smartcompus.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartcompus.dto.RequestDto.RequestDto;
import org.example.smartcompus.model.enums.RequestStatus;
import org.example.smartcompus.service.interfaces.IRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final IRequestService requestService;

    @PostMapping
    public ResponseEntity<RequestDto> submitRequest(@Valid @RequestBody RequestDto dto) {
        return new ResponseEntity<>(requestService.submitRequest(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RequestDto>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestDto> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.getRequestById(id));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<RequestDto>> getPendingRequests() {
        return ResponseEntity.ok(requestService.getAllPendingRequests());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<RequestDto>> getRequestsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(requestService.getRequestsByStudent(studentId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RequestDto> updateRequestStatus(
            @PathVariable Long id,
            @RequestParam RequestStatus status) {
        return ResponseEntity.ok(requestService.updateRequestStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        requestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}

