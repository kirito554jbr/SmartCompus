package org.example.smartcompus.service;

import lombok.RequiredArgsConstructor;
import org.example.smartcompus.Mappers.RoomMapper;
import org.example.smartcompus.dto.RoomDto.RoomDto;
import org.example.smartcompus.exceptions.ResourceNotFoundException;
import org.example.smartcompus.model.Room;
import org.example.smartcompus.repository.RoomRepository;
import org.example.smartcompus.service.interfaces.IRoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService implements IRoomService {
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Override
    public RoomDto getRoomById(Long id) {
        return roomRepository.findById(id)
                .map(roomMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Room " + id + " not found"));
    }

    @Override
    public List<RoomDto> getRooms() {
        return roomRepository.findAll().stream()
                .map(roomMapper::toDto)
                .toList();
    }

    @Override
    public RoomDto createRoom(RoomDto roomDto) {
        Room room = roomMapper.toEntity(roomDto);
        room = roomRepository.save(room);
        return roomMapper.toDto(room);
    }

    @Override
    @Transactional
    public RoomDto updateRoom(RoomDto roomDto, long id) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room " + id + " not found"));

        roomMapper.updateRoomFromDto(roomDto, existingRoom);

        return roomMapper.toDto(roomRepository.save(existingRoom));
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete: Room " + id + " not found");
        }
        roomRepository.deleteById(id);
    }
}
