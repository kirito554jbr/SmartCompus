package org.example.smartcompus.service.interfaces;

import org.example.smartcompus.dto.RoomDto.RoomDto;

import java.util.List;

public interface IRoomService {
    RoomDto getRoomById(Long id);
    List<RoomDto> getRooms();
    RoomDto createRoom(RoomDto roomDto);
    RoomDto updateRoom(RoomDto roomDto, long id);
    void deleteRoom(Long id);
}
