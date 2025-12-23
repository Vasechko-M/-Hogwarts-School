package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        logger.info("Was invoked method for createStudent");
        try {
            Student savedStudent = studentRepository.save(student);
            logger.debug("Created student: {}", savedStudent);
            return savedStudent;
        } catch (Exception e) {
            logger.error("Error occurred while creating student", e);
            throw e;
        }
    }

    public Student findStudent(long id) {
        logger.info("Was invoked method for findStudent with id={}", id);
        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Студент с id " + id + " не найден"));
            logger.debug("Found student: {}", student);
            return student;
        } catch (Exception e) {
            logger.error("Error occurred while finding student with id={}", id, e);
            throw e;
        }
    }

    public Student editStudent(Student student) {
        logger.info("Was invoked method for editStudent");
        try {
            Student updatedStudent = studentRepository.save(student);
            logger.debug("Updated student: {}", updatedStudent);
            return updatedStudent;
        } catch (Exception e) {
            logger.error("Error occurred while editing student", e);
            throw e;
        }
    }

    public void deleteStudent(long id) {
        logger.info("Was invoked method for deleteStudent with id={}", id);
        try {
            if (studentRepository.existsById(id)) {
                studentRepository.deleteById(id);
                logger.info("Deleted student with id={}", id);
            } else {
                logger.warn("Attempted to delete non-existent student with id={}", id);
            }
        } catch (Exception e) {
            logger.error("Error occurred while deleting student with id={}", id, e);
            throw e;
        }
    }

    public Collection<Student> getAllStudents() {
        logger.info("Was invoked method for getAllStudents");
        try {
            Collection<Student> students = studentRepository.findAll();
            logger.debug("Fetched {} students", students.size());
            return students;
        } catch (Exception e) {
            logger.error("Error occurred while fetching all students", e);
            throw e;
        }
    }

    public Collection<Student> getStudentsByAge(int age) {
        logger.info("Was invoked method for getStudentsByAge with age={}", age);
        if (age <= 0) {
            logger.warn("Invalid age provided: {}", age);
            return Collections.emptyList();
        }
        try {
            Collection<Student> students = studentRepository.findByAge(age);
            logger.debug("Found {} students with age={}", students.size(), age);
            return students;
        } catch (Exception e) {
            logger.error("Error occurred while fetching students by age", e);
            throw e;
        }
    }

    public Collection<Student> getStudentsByAgeBetween(int min, int max) {
        logger.info("Was invoked method for getStudentsByAgeBetween with min={} and max={}", min, max);
        if (min > max || min < 0 || max < 0) {
            logger.warn("Invalid min/max values: min={}, max={}", min, max);
            return Collections.emptyList();
        }
        try {
            Collection<Student> students = studentRepository.findByAgeBetween(min, max);
            logger.debug("Found {} students between ages {} and {}", students.size(), min, max);
            return students;
        } catch (Exception e) {
            logger.error("Error occurred while fetching students by age range", e);
            throw e;
        }
    }

    public long getTotalStudents() {
        logger.info("Was invoked method for getTotalStudents");
        try {
            long count = studentRepository.countAllStudents();
            logger.debug("Total students: {}", count);
            return count;
        } catch (Exception e) {
            logger.error("Error occurred while counting students", e);
            throw e;
        }
    }

//    public Double getAverageAge() {
//        logger.info("Was invoked method for getAverageAge");
//        try {
//            Double avgAge = studentRepository.getAverageAge();
//            logger.debug("Average age of students: {}", avgAge);
//            return avgAge;
//        } catch (Exception e) {
//            logger.error("Error occurred while calculating average age", e);
//            throw e;
//        }
//    }

    public List<Student> getLastFiveStudents() {
        logger.info("Was invoked method for getLastFiveStudents");
        try {
            List<Student> students = studentRepository.findLastFiveStudents();
            logger.debug("Last five students fetched: {}", students);
            return students;
        } catch (Exception e) {
            logger.error("Error occurred while fetching last five students", e);
            throw e;
        }
    }

    public List<String> getNamesStartingWithA() {
        List<Student> allStudents = studentRepository.findAll();

        return allStudents.stream()
                .map(Student::getName)
                .filter(name -> name != null && name.startsWith("А"))
                .map(String::toUpperCase)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public Double getAverageAge() {
        List<Student> allStudents = studentRepository.findAll();

        return allStudents.stream()
                .parallel()
                .filter(student -> student.getAge() != null)
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
    }
}