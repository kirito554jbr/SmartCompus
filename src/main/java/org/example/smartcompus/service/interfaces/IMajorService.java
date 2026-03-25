package org.example.smartcompus.service.interfaces;

import org.example.smartcompus.dto.MajorDto.MajorDto;

import java.util.List;

public interface IMajorService {
    MajorDto getMajorById(Long id);
    List<MajorDto> getMajors();
    MajorDto createMajor(MajorDto majorDto);
    MajorDto updateMajor(MajorDto majorDto, Long id);
    void deleteMajor(Long id);
}

