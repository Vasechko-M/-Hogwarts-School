package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.List;

@Tag(name = "Студенты")
@RestController
@RequestMapping("students")
public class StudentController {
    private StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(summary = "Показать студентов")
    @GetMapping
    public ResponseEntity<Collection<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @Operation(summary = "Добавить студента")
    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @Operation(summary = "Найти студента по id")
    @GetMapping("{id}")
    public ResponseEntity<Student> getStudentInfo(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(student);
    }

    @Operation(summary = "Редактировать студента")
    @PutMapping
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        Student foundStudent = studentService.editStudent(student);
        if (foundStudent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @Operation(summary = "Удалить студента")
    @DeleteMapping("{id}")
    public ResponseEntity deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Найти студентов по возрасту")
    @GetMapping("age/between")
    public ResponseEntity<Collection<Student>> getStudentsByAgeBetween(@RequestParam int min, @RequestParam int max) {
        Collection<Student> students = studentService.getStudentsByAgeBetween(min, max);
        return ResponseEntity.ok(students);
    }

    @Operation(summary = "Посмотреть факультет студента")
    @GetMapping("{id}/faculty")
    public ResponseEntity<Faculty> getFacultyByStudent(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        Faculty faculty = student.getFaculty();
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @Operation(summary = "Количество всех студентов")
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalStudents() {
        long total = studentService.getTotalStudents();
        return ResponseEntity.ok(total);
    }

    @Operation(summary = "Средний возраст студентов")
    @GetMapping("/average-age")
    public ResponseEntity<Double> getAverageAge() {
        Double avgAge = studentService.getAverageAge();
        return ResponseEntity.ok(avgAge);
    }

    @Operation(summary = "Пять последних студентов")
    @GetMapping("/last-five")
    public ResponseEntity<List<Student>> getLastFiveStudents() {
        return ResponseEntity.ok(studentService.getLastFiveStudents());
    }

    @Operation(summary = "Получить имена всех студентов, начинающихся с 'А', отсортированные в алфавитном порядке, в верхнем регистре")
    @GetMapping("/names/starts-with-a")
    public ResponseEntity<List<String>> getNamesStartingWithA() {
        List<String> names = studentService.getNamesStartingWithA();
        return ResponseEntity.ok(names);
    }
    @Operation(summary = "имена всех студентов в параллельном режиме")
    @GetMapping("/print-parallel")
    public ResponseEntity<String> printStudentsInParallel() throws InterruptedException {
        List<Student> students = (List<Student>) studentService.getAllStudents();
        try {
            studentService.getParallelStudentNames(students);
        } catch (InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка выполнения");
        }
        return ResponseEntity.ok("Вывод смотри в консоли");
    }

    @Operation(summary = "имена всех студентов в синхронном режиме")
    @GetMapping("/print-synchronized")
    public ResponseEntity<String> printNamesSynchronized() {
        try {
            String result = studentService.printNamesSynchronized();
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка выполнения потока");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла непредвиденная ошибка");
        }
    }


}

