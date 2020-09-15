package com.softserve.marathon.controller;

import com.softserve.marathon.model.Course;
import com.softserve.marathon.service.CourseService;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CourseControllerTest {
    private final MockMvc mockMvc;
    private final CourseService courseService;

    @Autowired
    public CourseControllerTest(MockMvc mockMvc, CourseService courseService) {
        this.mockMvc = mockMvc;
        this.courseService = courseService;
    }

    @Test
    public void showAllMarathonsTest() throws Exception {
        List<Course> expected = courseService.getAll();
        mockMvc.perform(MockMvcRequestBuilders.get("/marathons"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("marathons"))
                .andExpect(MockMvcResultMatchers.model().attribute("marathons", expected));
    }

    @Test
    public void addMarathonTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/marathons/add")
                .param("title", "marathon1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        assertEquals(1, courseService.getAll().stream()
                .filter(m -> m.getTitle().equals("marathon1"))
                .count());
    }

    @Test
    public void closeMarathonTest() throws Exception {
        //given
        Course course = new Course("marathon test");
        courseService.createOrUpdateCourse(course);
        assertEquals(false, course.isClosed());
        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/marathons/delete/" + course.getId()));
        //then
        Course courseFromDb = courseService.getCourseById(course.getId());
        assertEquals(true, courseFromDb.isClosed());
        assertEquals(course.getTitle(), courseFromDb.getTitle());
    }

    @Test
    public void updateMarathonTest() throws Exception {
        //given
        Course course = new Course("marathon test");
        courseService.createOrUpdateCourse(course);
        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/marathons/add")
                .param("id", String.valueOf(course.getId()))
                .param("title", "new marathon"));
        //then
        Course courseFromDb = courseService.getCourseById(course.getId());
        assertEquals("new marathon", courseFromDb.getTitle());
    }

    @Test
    public void updateOrCreateTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/marathons/add"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void updateTest() throws Exception {
        Course expected = new Course("marathon test");
        courseService.createOrUpdateCourse(expected);
        mockMvc.perform(MockMvcRequestBuilders.get("/marathons/edit/" + expected.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("marathon", expected));
    }
}
