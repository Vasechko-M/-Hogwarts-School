package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class StudentService {
    private StudentRepository studentRepository;
    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }
    public Student findStudent(long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Студент с id " + id + " не найден"));
    }

    public Student editStudent(Student student) {
        return studentRepository.save(student);
    }

    public void deleteStudent(long id) {
        studentRepository.deleteById(id);
    }

    public Collection<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Collection<Student> getStudentsByAge(int age) {
            if (age <= 0) {
                return Collections.emptyList();
            }
            return studentRepository.findByAge(age);
        }
    public Collection<Student> getStudentsByAgeBetween(int min, int max) {
        if (min > max || min < 0 || max < 0) {
            return Collections.emptyList(); //
        }
        return studentRepository.findByAgeBetween(min, max);
    }

    public long getTotalStudents() {
        return studentRepository.countAllStudents();
    }

    public Double getAverageAge() {
        return studentRepository.getAverageAge();
    }

    public List<Student> getLastFiveStudents() {
        return studentRepository.findLastFiveStudents();
    }

}
