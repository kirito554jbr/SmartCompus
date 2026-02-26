package org.example.smartcompus.service.interfaces;

import org.example.smartcompus.dto.RequestDto.RequestDto;
import org.example.smartcompus.model.enums.RequestStatus;

import java.util.List;

public interface IRequestService {
    RequestDto submitRequest(RequestDto dto);
    RequestDto updateRequestStatus(Long requestId, RequestStatus status);
    List<RequestDto> getAllPendingRequests();
    List<RequestDto> getRequestsByStudent(Long studentId);
    RequestDto getRequestById(Long id);
    void deleteRequest(Long id); // If a student cancels their request
    List<RequestDto> getAllRequests();
}

