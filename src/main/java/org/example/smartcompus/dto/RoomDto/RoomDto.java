package org.example.smartcompus.dto.RoomDto;

import lombok.Data;

@Data
public class RoomDto {
    private Long id;
    private String name;
    private int capacity;
    private String type;
}