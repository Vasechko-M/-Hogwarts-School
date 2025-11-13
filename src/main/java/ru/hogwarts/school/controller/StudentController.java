package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

@RestController
@RequestMapping("students")
public class StudentController {
    private StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudentInfo(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(student);
    }

    @PutMapping
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        Student foundStudent = studentService.editStudent(student);
        if (foundStudent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("age/between")
    public ResponseEntity<Collection<Student>> getStudentsByAgeBetween(@RequestParam int min, @RequestParam int max) {
        Collection<Student> students = studentService.getStudentsByAgeBetween(min, max);
        return ResponseEntity.ok(students);
    }

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

}

