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
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StudentController.class)
class StudentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Получение всех студентов")
    void testGetAllStudents() throws Exception {
        Student student1 = new Student();
        student1.setId(1);
        student1.setName("Студент 1");
        Student student2 = new Student();
        student2.setId(2);
        student2.setName("Студент 2");
        when(studentService.getAllStudents()).thenReturn(Arrays.asList(student1, student2));

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Студент 1"))
                .andExpect(jsonPath("$[1].name").value("Студент 2"));
    }

    @Test
    @DisplayName("Тест добавление студентов")
    void testCreateStudent() throws Exception {
        Student newStudent = new Student();
        newStudent.setName("Тестовый студент");
        when(studentService.createStudent(any(Student.class))).thenReturn(newStudent);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Тестовый студент"));
    }

    @Test
    @DisplayName("Получение студента по id если он есть")
    void testGetStudentInfo_Found() throws Exception {
        Student student = new Student();
        student.setId(10);
        student.setName("Тестовый студент");
        when(studentService.findStudent(10)).thenReturn(student);

        mockMvc.perform(get("/students/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Тестовый студент"));
    }

    @Test
    @DisplayName("Получение студента по id если его нет")
    void testGetStudentInfo_NotFound() throws Exception {
        when(studentService.findStudent(99)).thenReturn(null);

        mockMvc.perform(get("/students/99"))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("Редактирование студента, если он есть")
    void testEditStudent_Success() throws Exception {
        Student student = new Student();
        student.setId(1);
        student.setName("Тестовый студент");
        when(studentService.editStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(put("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Тестовый студент"));
    }

    @Test
    @DisplayName("Редактирование студента, если его нет")
    void testEditStudent_NotFound() throws Exception {
        Student student = new Student();
        student.setId(99);
        student.setName("Несуществующий студент");
        when(studentService.editStudent(any(Student.class))).thenReturn(null);

        mockMvc.perform(put("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Тест удаления студента")
    void testDeleteStudent() throws Exception {
        doNothing().when(studentService).deleteStudent(1);

        mockMvc.perform(delete("/students/1"))
                .andExpect(status().isOk());

        verify(studentService, times(1)).deleteStudent(1);
    }

    @Test
    @DisplayName("Поиск студентов по возрасту")
    void testGetStudentsByAgeBetween() throws Exception {
        Student student = new Student();
        student.setId(1);
        student.setName("Тестовый студент");
        when(studentService.getStudentsByAgeBetween(18, 25))
                .thenReturn(Collections.singletonList(student));

        mockMvc.perform(get("/students/age/between")
                        .param("min", "18")
                        .param("max", "25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Тестовый студент"));
    }

    @Test
    @DisplayName("Просмотр факультета студента")
    void testGetFacultyByStudent_Found() throws Exception {
        Faculty faculty = new Faculty();
        //faculty.setId(1);
        faculty.setName("Тестовый факультет");
        Student student = new Student();
        //student.setId(10);
        student.setName("Тестовый студент");
        student.setFaculty(faculty);
        when(studentService.findStudent(10)).thenReturn(student);

        mockMvc.perform(get("/students/10/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Тестовый факультет"));
    }

    @Test
    @DisplayName("Просмотр факультета студента, если факультет не найден")
    void testGetFacultyByStudent_NotFound() throws Exception {
        Student student = new Student();
        //student.setId(2); по правилам хорошего тона пишут, что нужно писать id, но работает и без него (в других местах то же самое)
        student.setName("Тестовый студент");
        student.setFaculty(null);
        when(studentService.findStudent(2)).thenReturn(student);

        mockMvc.perform(get("/students/2/faculty"))
                .andExpect(status().isNotFound());
    }
}