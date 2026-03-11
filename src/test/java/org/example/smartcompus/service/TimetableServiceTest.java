package org.example.smartcompus.service;

import org.example.smartcompus.Mappers.TimetableMapper;
import org.example.smartcompus.dto.TimetableDto.TimetableDto;
import org.example.smartcompus.exceptions.ConflictException;
import org.example.smartcompus.exceptions.ResourceNotFoundException;
import org.example.smartcompus.model.Course;
import org.example.smartcompus.model.Room;
import org.example.smartcompus.model.Teacher;
import org.example.smartcompus.model.Timetable;
import org.example.smartcompus.repository.CourseRepository;
import org.example.smartcompus.repository.RoomRepository;
import org.example.smartcompus.repository.TimetableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimetableServiceTest {

    @Mock private TimetableRepository timetableRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private TimetableMapper timetableMapper;

    @InjectMocks
    private TimetableService timetableService;

    private Course course;
    private Room room;
    private TimetableDto inputDto;

    @BeforeEach
    void setUp() {
        Teacher teacher = new Teacher();
        teacher.setIdUser(1L);
        teacher.setFirstName("Jane");
        teacher.setLastName("Smith");

        course = new Course();
        course.setIdCourse(10L);
        course.setName("Calculus");
        course.setCode("MATH101");
        course.setTeacher(teacher);

        room = new Room();
        room.setId(5L);
        room.setName("Lab 101");
        room.setCapacity(30);
        room.setType("PRACTICAL");

        inputDto = new TimetableDto();
        inputDto.setDay("Monday");
        inputDto.setStartTime(LocalTime.of(9, 0));
        inputDto.setEndTime(LocalTime.of(11, 0));
        inputDto.setCourseId(10L);
        inputDto.setRoomId(5L);
    }

    // ===================== Create Timetable (Happy Path) =====================

    @Test
    @DisplayName("Create timetable - valid course/room mapping should return TimetableDto")
    void createSchedule_ValidMapping_ShouldReturnTimetableDto() {
        // Arrange
        Timetable timetableEntity = new Timetable();
        timetableEntity.setDay("Monday");
        timetableEntity.setStartTime(LocalTime.of(9, 0));
        timetableEntity.setEndTime(LocalTime.of(11, 0));

        Timetable savedTimetable = new Timetable();
        savedTimetable.setIdTimetable(1L);
        savedTimetable.setDay("Monday");
        savedTimetable.setStartTime(LocalTime.of(9, 0));
        savedTimetable.setEndTime(LocalTime.of(11, 0));
        savedTimetable.setCourse(course);
        savedTimetable.setRoom(room);

        TimetableDto expectedDto = new TimetableDto();
        expectedDto.setId(1L);
        expectedDto.setDay("Monday");
        expectedDto.setStartTime(LocalTime.of(9, 0));
        expectedDto.setEndTime(LocalTime.of(11, 0));
        expectedDto.setCourseId(10L);
        expectedDto.setCourseName("Calculus");
        expectedDto.setRoomId(5L);
        expectedDto.setRoomName("Lab 101");

        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(roomRepository.findById(5L)).thenReturn(Optional.of(room));
        when(timetableRepository.existsOverlap(eq(5L), eq("Monday"), any(), any())).thenReturn(false);
        when(timetableMapper.toEntity(inputDto)).thenReturn(timetableEntity);
        when(timetableRepository.save(timetableEntity)).thenReturn(savedTimetable);
        when(timetableMapper.toDto(savedTimetable)).thenReturn(expectedDto);

        // Act
        TimetableDto result = timetableService.createSchedule(inputDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDay()).isEqualTo("Monday");
        assertThat(result.getCourseId()).isEqualTo(10L);
        assertThat(result.getCourseName()).isEqualTo("Calculus");
        assertThat(result.getRoomId()).isEqualTo(5L);
        assertThat(result.getRoomName()).isEqualTo("Lab 101");
    }

    // ===================== Create Timetable - sets course and room =====================

    @Test
    @DisplayName("Create timetable - should set course and room on entity before saving")
    void createSchedule_ShouldSetCourseAndRoom() {
        // Arrange
        Timetable timetableEntity = new Timetable();

        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(roomRepository.findById(5L)).thenReturn(Optional.of(room));
        when(timetableRepository.existsOverlap(eq(5L), eq("Monday"), any(), any())).thenReturn(false);
        when(timetableMapper.toEntity(inputDto)).thenReturn(timetableEntity);

        Timetable savedTimetable = new Timetable();
        savedTimetable.setCourse(course);
        savedTimetable.setRoom(room);
        when(timetableRepository.save(timetableEntity)).thenReturn(savedTimetable);
        when(timetableMapper.toDto(savedTimetable)).thenReturn(new TimetableDto());

        // Act
        timetableService.createSchedule(inputDto);

        // Assert
        verify(timetableRepository).save(argThat(t ->
                t.getCourse() == course && t.getRoom() == room
        ));
    }

    // ===================== Course Not Found =====================

    @Test
    @DisplayName("Create timetable - course not found should throw ResourceNotFoundException")
    void createSchedule_CourseNotFound_ShouldThrow() {
        // Arrange
        when(courseRepository.findById(10L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> timetableService.createSchedule(inputDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course not found");
    }

    // ===================== Room Not Found =====================

    @Test
    @DisplayName("Create timetable - room not found should throw ResourceNotFoundException")
    void createSchedule_RoomNotFound_ShouldThrow() {
        // Arrange
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(roomRepository.findById(5L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> timetableService.createSchedule(inputDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Room not found");
    }

    // ===================== Room Not Available (Conflict) =====================

    @Test
    @DisplayName("Create timetable - room occupied should throw ConflictException")
    void createSchedule_RoomOccupied_ShouldThrowConflict() {
        // Arrange
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(roomRepository.findById(5L)).thenReturn(Optional.of(room));
        when(timetableRepository.existsOverlap(eq(5L), eq("Monday"),
                eq(LocalTime.of(9, 0)), eq(LocalTime.of(11, 0)))).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> timetableService.createSchedule(inputDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Room is already occupied");
    }

    // ===================== Room Availability Check =====================

    @Test
    @DisplayName("isRoomAvailable - no overlap should return true")
    void isRoomAvailable_NoOverlap_ShouldReturnTrue() {
        // Arrange
        when(timetableRepository.existsOverlap(5L, "Monday",
                LocalTime.of(9, 0), LocalTime.of(11, 0))).thenReturn(false);

        // Act
        boolean available = timetableService.isRoomAvailable(5L, "Monday",
                LocalTime.of(9, 0), LocalTime.of(11, 0));

        // Assert
        assertThat(available).isTrue();
    }

    @Test
    @DisplayName("isRoomAvailable - overlap exists should return false")
    void isRoomAvailable_OverlapExists_ShouldReturnFalse() {
        // Arrange
        when(timetableRepository.existsOverlap(5L, "Monday",
                LocalTime.of(9, 0), LocalTime.of(11, 0))).thenReturn(true);

        // Act
        boolean available = timetableService.isRoomAvailable(5L, "Monday",
                LocalTime.of(9, 0), LocalTime.of(11, 0));

        // Assert
        assertThat(available).isFalse();
    }

    // ===================== Verify save is called =====================

    @Test
    @DisplayName("Create timetable - should save to repository")
    void createSchedule_ShouldSaveToRepository() {
        // Arrange
        Timetable entity = new Timetable();
        Timetable saved = new Timetable();
        saved.setCourse(course);
        saved.setRoom(room);

        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(roomRepository.findById(5L)).thenReturn(Optional.of(room));
        when(timetableRepository.existsOverlap(eq(5L), eq("Monday"), any(), any())).thenReturn(false);
        when(timetableMapper.toEntity(inputDto)).thenReturn(entity);
        when(timetableRepository.save(entity)).thenReturn(saved);
        when(timetableMapper.toDto(saved)).thenReturn(new TimetableDto());

        // Act
        timetableService.createSchedule(inputDto);

        // Assert
        verify(timetableRepository).save(any(Timetable.class));
    }
}

