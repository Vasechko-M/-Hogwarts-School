package ru.hogwarts.school.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private FacultyController facultyController;
    @Autowired
    private TestRestTemplate restTemplate;
    private String getBaseUrl() {
        return "http://localhost:" + port + "/faculties";
    }
    @Test
    @DisplayName("Получение всех факультетов")
    void testGetAllFaculties() {
        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(getBaseUrl(), Faculty[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
    @Test
    @DisplayName("Создание факультетов")
    void testCreateFaculty() {
        Faculty newFaculty = new Faculty();
        newFaculty.setName("Тестовый факультет");
        newFaculty.setColor("Малиновый");

        ResponseEntity<Faculty> response = restTemplate.postForEntity(getBaseUrl(), newFaculty, Faculty.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Faculty created = response.getBody();
        assertNotNull(created);
        assertEquals("Тестовый факультет", created.getName());
        assertEquals("Малиновый", created.getColor());
    }

    @Test
    @DisplayName("Получение факультета по id")
    void testGetFacultyInfo() {
        Faculty faculty = new Faculty();
        faculty.setName("Тестовый факультет для получения  по id");
        faculty.setColor("Еще малиновый");
        Faculty created = restTemplate.postForObject(getBaseUrl(), faculty, Faculty.class);
        Long id = created.getId();

        ResponseEntity<Faculty> response = restTemplate.getForEntity(getBaseUrl() + "/" + id, Faculty.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Тестовый факультет для получения  по id", response.getBody().getName());
    }
    @Test
    @DisplayName("Проверка редактирования факультета")
    void testEditFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Факультет не важен");
        faculty.setColor("Не малиновый");
        Faculty created = restTemplate.postForObject(getBaseUrl(), faculty, Faculty.class);

        created.setName("Теперь важен факультет");
        created.setColor("Снова малиновы");
        HttpEntity<Faculty> requestUpdate = new HttpEntity<>(created);

        ResponseEntity<Faculty> response = restTemplate.exchange(getBaseUrl(), HttpMethod.PUT, requestUpdate, Faculty.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Теперь важен факультет", response.getBody().getName());
        assertEquals("Снова малиновы", response.getBody().getColor());
    }
    @Test
    @DisplayName("Тест на удаление факультета")
    void testDeleteFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("бла-бла-бла");
        faculty.setColor("та-ра-ра");
        Faculty created = restTemplate.postForObject(getBaseUrl(), faculty, Faculty.class);
        Long id = created.getId();

        restTemplate.delete(getBaseUrl() + "/" + id);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(getBaseUrl() + "/" + id, Faculty.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    @Test
    @DisplayName("Поиск факультета по имени или цвету")
    void testSearchFaculties() {
        String query = "Пурпурный";
        ResponseEntity<Collection> response = restTemplate.getForEntity(getBaseUrl() + "/search?query=" + query, Collection.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
    @Test
    @DisplayName("Получение студентов по id факультета")
    void testGetStudentsByFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Тестовый факультет для поиска студентов на нем");
        faculty.setColor("И здесь малиновый");
        Faculty createdFaculty = restTemplate.postForObject(getBaseUrl(), faculty, Faculty.class);
        Long id = createdFaculty.getId();

        Student student = new Student();
        student.setName("Тестовый студент для получение студентов по id факультета");
        student.setFaculty(createdFaculty);  // Устанавливаем связь через объект Faculty (JPA обработает)
        restTemplate.postForObject("http://localhost:" + port + "/students", student, Student.class);  // Используем полный URL для /students (предполагаем StudentController)

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                getBaseUrl() + "/" + id + "/students",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {}
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals("Тестовый студент для получение студентов по id факультета", response.getBody().get(0).getName());
        assertEquals(createdFaculty.getId(), response.getBody().get(0).getFaculty().getId());
    }

}