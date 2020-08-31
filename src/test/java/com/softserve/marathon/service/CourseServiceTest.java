package com.softserve.marathon.service;

import com.softserve.marathon.model.Course;
import com.softserve.marathon.repository.CourseRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @MockBean
    private CourseRepository courseRepository;

    @Test
    public void getAllTest() {
        //Given
        List<Course> expected = new ArrayList<>();
        Course course = new Course();
        course.setTitle("Marathon 1");
        expected.add(course);
        //When
        when(courseRepository.findAll()).thenReturn(expected);
        //Then
        List<Course> actual = courseService.getAll();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getMarathonByIdTest() {
        //Given
        Course expected = new Course();
        expected.setId(2L);
        expected.setTitle("Marathon 2");
        //When
        when(courseRepository.findById(2L)).thenReturn(Optional.of(expected));
        //Then
        Course actual = courseService.getCourseById(2L);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createOrUpdateMarathonTest() {
        //Given
        Course expected = new Course();
        expected.setId(3L);
        expected.setTitle("Marathon 3");
        //When
        when(courseRepository.save(any())).thenReturn(expected);
        //Then
        Course actual = courseService.createOrUpdateCourse(expected);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void deleteMarathonByIdTest() {
        Course expected = new Course();
        expected.setId(4L);
        expected.setTitle("Marathon 4");
        courseRepository.save(expected);
        when(courseRepository.findById(4L)).thenReturn(Optional.of(expected));
        doNothing().when(courseRepository).deleteById(any());
        courseService.deleteCourseById(4L);
        verify(courseRepository).deleteById(any());
    }
}
