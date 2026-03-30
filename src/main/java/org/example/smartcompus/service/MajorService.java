package org.example.smartcompus.service;

import lombok.RequiredArgsConstructor;
import org.example.smartcompus.Mappers.MajorMapper;
import org.example.smartcompus.dto.MajorDto.MajorDto;
import org.example.smartcompus.exceptions.ConflictException;
import org.example.smartcompus.exceptions.ResourceNotFoundException;
import org.example.smartcompus.model.Major;
import org.example.smartcompus.repository.MajorRepository;
import org.example.smartcompus.repository.StudentRepository;
import org.example.smartcompus.service.interfaces.IMajorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MajorService implements IMajorService {
    private final MajorRepository majorRepository;
    private final StudentRepository studentRepository;
    private final MajorMapper majorMapper;

    @Override
    public MajorDto getMajorById(Long id) {
        return majorRepository.findById(id)
                .map(majorMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Major " + id + " not found"));
    }

    @Override
    public List<MajorDto> getMajors() {
        return majorRepository.findAll().stream()
                .map(majorMapper::toDto)
                .toList();
    }

    @Override
    public MajorDto createMajor(MajorDto majorDto) {
        String normalizedName = normalizeName(majorDto.getName());
        if (majorRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new ConflictException("Major with name " + normalizedName + " already exists");
        }

        Major major = majorMapper.toEntity(majorDto);
        major.setName(normalizedName);

        return majorMapper.toDto(majorRepository.save(major));
    }

    @Override
    public MajorDto updateMajor(MajorDto majorDto, Long id) {
        Major existingMajor = majorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Major " + id + " not found"));

        majorMapper.updateMajorFromDto(majorDto, existingMajor);

        if (majorDto.getName() != null) {
            String normalizedName = normalizeName(majorDto.getName());
            majorRepository.findByNameIgnoreCase(normalizedName)
                    .filter(major -> !major.getIdMajor().equals(existingMajor.getIdMajor()))
                    .ifPresent(major -> {
                        throw new ConflictException("Major with name " + normalizedName + " already exists");
                    });
            existingMajor.setName(normalizedName);
        }

        return majorMapper.toDto(majorRepository.save(existingMajor));
    }

    @Override
    public void deleteMajor(Long id) {
        if (!majorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete: Major " + id + " not found");
        }

        if (studentRepository.existsByMajor_IdMajor(id)) {
            throw new ConflictException("Cannot delete: Major " + id + " has assigned students");
        }

        majorRepository.deleteById(id);
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Major name is required");
        }
        return name.trim();
    }
}

