package org.example.smartcompus.service.interfaces;

import org.example.smartcompus.dto.RoomDto.RoomDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IRoomService {
    RoomDto getRoomById(Long id);
    List<RoomDto> getRooms();
    Page<RoomDto> getRoomsPaginated(int page, int size, String sortBy, String sortDirection);
    RoomDto createRoom(RoomDto roomDto);
    RoomDto updateRoom(RoomDto roomDto, long id);
    void deleteRoom(Long id);
}
