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



    public void getParallelStudentNames(List<Student> students) throws InterruptedException {
        System.out.println("Параллельный режим");
        int size = students.size();

        final double[] times = new double[3];

        long overallStart = System.nanoTime();

        for (int i=0; i<Math.min(2, size); i++) {
            System.out.println("Поток 1: " + students.get(i).getName());
        }

        Thread t1 = new Thread(() -> {
            long start = System.nanoTime();
            int threadNumber = 2;
            for (int i = 2; i <= 3 && i < size; i++) {
                System.out.println("Поток " + threadNumber + ": " + students.get(i).getName());
            }
            long end = System.nanoTime();
            times[0] = (end - start) / 1_000_000.0;
        });

        Thread t2 = new Thread(() -> {
            long start = System.nanoTime();
            int threadNumber = 3;
            for (int i=4; i<=5 && i< size; i++) {
                System.out.println("Поток " + threadNumber + ": " + students.get(i).getName());
            }
            long end = System.nanoTime();
            times[1] = (end - start) / 1_000_000.0;
        });

        Thread t3 = new Thread(() -> {
            long start = System.nanoTime();
            int threadNumber = 4;
            for (int i=6; i< size; i++) {
                System.out.println("Поток " + threadNumber + ": " + students.get(i).getName());
            }
            long end = System.nanoTime();
            times[2] = (end - start) / 1_000_000.0;
        });

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        long overallEnd = System.nanoTime();

        System.out.printf("Общее время выполнения: %.3f мс%n", (overallEnd - overallStart) / 1_000_000.0);
        System.out.printf("Время выполнения Поток 2: %.3f мс%n", times[0]);
        System.out.printf("Время выполнения Поток 3: %.3f мс%n", times[1]);
        System.out.printf("Время выполнения Поток 4: %.3f мс%n", times[2]);
    }

    public String printNamesSynchronized() throws InterruptedException {
        System.out.println("Синхронный режим");
        Collection<Student> allStudents = getAllStudents();

        List<String> allNames = allStudents.stream()
                .map(Student::getName)
                .collect(Collectors.toList());

        System.out.println("Всего студентов: " + allNames.size());
        System.out.println("Имена: " + allNames);

        if (allNames.size() < 6) {
            throw new IllegalArgumentException("Недостаточно студентов для выполнения задачи");
        }

        System.out.println("Основной поток:");
        System.out.println(allNames.get(0));
        System.out.println(allNames.get(1));

        final double[] times = new double[3];

        Thread thread1 = new Thread(() -> {
            long start = System.nanoTime();
            System.out.println("Параллельный поток 1:");
            System.out.println(allNames.get(2));
            System.out.println(allNames.get(3));
            long end = System.nanoTime();
            times[0] = (end - start) / 1_000_000.0;
        });

        Thread thread2 = new Thread(() -> {
            long start = System.nanoTime();
            System.out.println("Параллельный поток 2:");
            System.out.println(allNames.get(4));
            System.out.println(allNames.get(5));
            long end = System.nanoTime();
            times[1] = (end - start) / 1_000_000.0;
        });

        Thread thread3 = new Thread(() -> {
            long start = System.nanoTime();
            System.out.println("Параллельный поток 3:");
            for (int i = 6; i < allNames.size(); i++) {
                System.out.println(allNames.get(i));
            }
            long end = System.nanoTime();
            times[2] = (end - start) / 1_000_000.0;
        });

        long overallStart = System.nanoTime();

        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();

        long overallEnd = System.nanoTime();

        System.out.printf("Общее время выполнения всех потоков: %.3f мс%n", (overallEnd - overallStart) / 1_000_000.0);
        System.out.printf("Время выполнения Поток 1: %.3f мс%n", times[0]);
        System.out.printf("Время выполнения Поток 2: %.3f мс%n", times[1]);
        System.out.printf("Время выполнения Поток 3: %.3f мс%n", times[2]);

        return "Имена студентов выведены в консоль";
    }

}