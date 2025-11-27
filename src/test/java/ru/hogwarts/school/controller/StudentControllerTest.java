package ru.hogwarts.school.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/students";
    }

    @Test
    @DisplayName("Получение всех студентов")
    void testGetAllStudents() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity(getBaseUrl(), Student[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Создание студентов")
    void testCreateStudent() {
        Student newStudent = new Student();
        newStudent.setName("Тестовы студент для теста создания студентов");
        newStudent.setAge(20);
        ResponseEntity<Student> response = restTemplate.postForEntity(getBaseUrl(), newStudent, Student.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Student created = response.getBody();
        assertNotNull(created);
        assertEquals("Тестовы студент для теста создания студентов", created.getName());
        assertEquals(20, created.getAge());
    }

    @Test
    @DisplayName("Получение студентов по id")
    void testGetStudentInfo() {
        Student student = new Student();
        student.setName("Тестовый студент для получения по id");
        student.setAge(22);
        Student created = restTemplate.postForObject(getBaseUrl(), student, Student.class);
        Long id = created.getId();

        ResponseEntity<Student> response = restTemplate.getForEntity(getBaseUrl() + "/" + id, Student.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Тестовый студент для получения по id", response.getBody().getName());
    }

    @Test
    @DisplayName("Редактирование студентов по id")
    void testEditStudent() {
        Student student = new Student();
        student.setName("Тестовый студент для редактирования по id");
        student.setAge(25);
        Student created = restTemplate.postForObject(getBaseUrl(), student, Student.class);

        created.setName("Тестовый студент для редактирования по id изменен");
        created.setAge(26);
        HttpEntity<Student> requestUpdate = new HttpEntity<>(created);
        ResponseEntity<Student> response = restTemplate.exchange(getBaseUrl(), HttpMethod.PUT, requestUpdate, Student.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Тестовый студент для редактирования по id изменен", response.getBody().getName());
        assertEquals(26, response.getBody().getAge());
    }

    @Test
    @DisplayName("Удаление студента")
    void testDeleteStudent() {
        Student student = new Student();
        student.setName("Двойник Гарри Поттера");
        student.setAge(15);
        Long id = restTemplate.postForObject(getBaseUrl(), student, Student.class).getId();

        restTemplate.delete(getBaseUrl() + "/" + id);

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/" + id, String.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Поиск студента по возрасту")
    void testGetStudentsByAgeBetween() {

        ResponseEntity<Student[]> response = restTemplate.getForEntity(getBaseUrl() + "/age/between?min=18&max=25", Student[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }


    @Test
    @DisplayName("Получения факультета студента. Нет факультета - пустое тело")
    void testGetFacultyByStudent() {
        Student student = new Student();
        student.setName("Student With Faculty");
        student.setAge(21);
        Student created = restTemplate.postForObject(getBaseUrl(), student, Student.class);
        Long id = created.getId();

        ResponseEntity<Faculty> response = restTemplate.getForEntity(getBaseUrl() + "/" + id + "/faculty", Faculty.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

    }
}