package org.example.smartcompus.service;

import lombok.RequiredArgsConstructor;
import org.example.smartcompus.Mappers.RequestMapper;
import org.example.smartcompus.dto.RequestDto.RequestDto;
import org.example.smartcompus.model.Request;
import org.example.smartcompus.model.enums.RequestStatus;
import org.example.smartcompus.repository.RequestRepository;
import org.example.smartcompus.service.interfaces.IRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestService implements IRequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    public RequestDto submitRequest(RequestDto dto) {
        Request request = requestMapper.toEntity(dto);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        Request savedRequest = requestRepository.save(request);
        return requestMapper.toDto(savedRequest);
    }

    @Override
    @Transactional
    public RequestDto updateRequestStatus(Long requestId, RequestStatus status) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));

        request.setStatus(status);
        request.setUpdatedAt(LocalDateTime.now());

        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getAllPendingRequests() {
        return requestRepository.findByStatus(RequestStatus.PENDING).stream()
                .map(requestMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequestsByStudent(Long studentId) {
        return requestRepository.findByStudent_IdUser(studentId).stream()
                .map(requestMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RequestDto getRequestById(Long id) {
        return requestRepository.findById(id)
                .map(requestMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Request not found"));
    }

    @Override
    public void deleteRequest(Long id) {
        if (!requestRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete: Request not found");
        }
        requestRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getAllRequests() {
        return requestRepository.findAll().stream()
                .map(requestMapper::toDto)
                .toList();
    }
}
