package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(FacultyController.class)
class FacultyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Получение всех факультетов")
    void testGetAllFaculties() throws Exception {
        Faculty faculty1 = new Faculty();
        faculty1.setId(1);
        faculty1.setName("Тестовый факультет");
        Faculty faculty2 = new Faculty();
        faculty2.setId(2);
        faculty2.setName("Еще тестовый факультет");
        when(facultyService.getAllFaculties()).thenReturn(Arrays.asList(faculty1, faculty2));

        mockMvc.perform(get("/faculties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Тестовый факультет"))
                .andExpect(jsonPath("$[1].name").value("Еще тестовый факультет"));
    }

    @Test
    @DisplayName("Создание факультетов")
    void testCreateFaculty() throws Exception {
        Faculty newFaculty = new Faculty();
        newFaculty.setName("Тестовый факультет");
        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(newFaculty);

        mockMvc.perform(post("/faculties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFaculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Тестовый факультет"));
    }

    @Test
    @DisplayName("Получение факультета по id, если он есть")
    void testGetFacultyInfo_Found() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1);
        faculty.setName("Тестовый факультет");
        when(facultyService.findFaculty(1)).thenReturn(faculty);

        mockMvc.perform(get("/faculties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Тестовый факультет"));
    }

    @Test
    @DisplayName("Получение факультета по id, если его нет")
    void testGetFacultyInfo_NotFound() throws Exception {
        when(facultyService.findFaculty(99)).thenReturn(null);

        mockMvc.perform(get("/faculties/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Редактирование факультета, если он есть")
    void testEditFaculty_Success() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1);
        faculty.setName("Тестовый факультет для редактирования");
        when(facultyService.editFaculty(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(put("/faculties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Тестовый факультет для редактирования"));
    }

    @Test
    @DisplayName("Редактирование факультета, если его нет")
    void testEditFaculty_NotFound() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(99);
        faculty.setName("Тестовый факультет");
        when(facultyService.editFaculty(any(Faculty.class))).thenReturn(null);

        mockMvc.perform(put("/faculties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Удаление факультета")
    void testDeleteFaculty() throws Exception {
        doNothing().when(facultyService).deleteFaculty(anyLong());

        mockMvc.perform(delete("/faculties/1"))
                .andExpect(status().isOk());

        verify(facultyService, times(1)).deleteFaculty(1);
    }

    @Test
    @DisplayName("Поиск факультета по имени или цвету")
    void testSearchFaculties() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1);
        faculty.setName("Тестовый факультет");
        when(facultyService.searchFacultiesByNameOrColor("Тестовый факультет"))
                .thenReturn(Collections.singletonList(faculty));

        mockMvc.perform(get("/faculties/search")
                        .param("query", "Тестовый факультет"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Тестовый факультет"));
    }

    @Test
    @DisplayName("Получение студентов по id факультета")
    void testGetStudentsByFaculty() throws Exception {
        Student student1 = new Student();
        student1.setId(1);
        student1.setName("Тестовый студент 1");
        Student student2 = new Student();
        student2.setId(2);
        student2.setName("Тестовый студент 2");
        Faculty faculty = new Faculty();
        faculty.setId(10);
        faculty.setName("Тестовый факультет");
        faculty.setStudents(Arrays.asList(student1, student2));

        when(facultyService.findFaculty(10)).thenReturn(faculty);

        mockMvc.perform(get("/faculties/10/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Тестовый студент 1"))
                .andExpect(jsonPath("$[1].name").value("Тестовый студент 2"));
    }

    @Test
    @DisplayName("Получение студентов по id факультета, если такого факультета нет")
    void testGetStudentsByFaculty_NotFound() throws Exception {
        when(facultyService.findFaculty(99)).thenReturn(null);

        mockMvc.perform(get("/faculties/99/students"))
                .andExpect(status().isNotFound());
    }
}