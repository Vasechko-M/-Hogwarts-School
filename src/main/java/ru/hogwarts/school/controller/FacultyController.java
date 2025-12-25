package ru.hogwarts.school.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;

@Tag(name = "Факультеты")
@RestController
@RequestMapping("faculties")
public class FacultyController {
    private FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @Operation(summary = "Показать факультеты")
    @GetMapping
    public Collection<Faculty> getAllFaculties() {
        return facultyService.getAllFaculties();
    }

    @Operation(summary = "Добавить факультет")
    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @Operation(summary = "Показать факультет по id")
    @GetMapping("{id}")
    public Faculty getFacultyInfo(@PathVariable Long id) {
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Faculty not found");
        }
        return faculty;
    }

    @Operation(summary = "Редактировать факультет")
    @PutMapping
    public Faculty editFaculty(@RequestBody Faculty faculty) {
        Faculty updatedFaculty = facultyService.editFaculty(faculty);
        if (updatedFaculty == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Faculty not found");
        }
        return updatedFaculty;
    }

    @Operation(summary = "Удалить факультет по id")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok().build();
        }

    @Operation(summary = "Найти факультет по цвету")
    @GetMapping("search")
    public ResponseEntity<Collection<Faculty>> searchFaculties(@RequestParam String query) {
        Collection<Faculty> faculties = facultyService.searchFacultiesByNameOrColor(query);
        return ResponseEntity.ok(faculties);
    }

    @Operation(summary = "Найти студентов по id факультета")
    @GetMapping("{id}/students")
    public ResponseEntity<Collection<Student>> getStudentsByFaculty(@PathVariable Long id) {
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }

        Collection<Student> students = faculty.getStudents();
        return ResponseEntity.ok(students);
    }
    @Operation(summary = "Получить самое длинное название факультета")
    @GetMapping("longest-name")
    public ResponseEntity<String> getLongestFacultyName() {
        String longestName = facultyService.getLongestFacultyName();
        if (longestName == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Факультеты не найдены");
        }
        return ResponseEntity.ok(longestName);
    }
}
