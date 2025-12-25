package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repositories.FacultyRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

@Service
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        logger.info("Was invoked method for createFaculty");
        try {
            Faculty savedFaculty = facultyRepository.save(faculty);
            logger.debug("Saved faculty: {}", savedFaculty);
            return savedFaculty;
        } catch (Exception e) {
            logger.error("Error occurred while creating faculty", e);
            throw e;
        }
    }

    public Faculty findFaculty(long id) {
        logger.info("Was invoked method for findFaculty with id={}", id);
        try {
            Faculty faculty = facultyRepository.findById(id).orElse(null);
            if (faculty == null) {
                logger.warn("No faculty found with id={}", id);
            } else {
                logger.debug("Found faculty: {}", faculty);
            }
            return faculty;
        } catch (Exception e) {
            logger.error("Error occurred while finding faculty with id={}", id, e);
            throw e;
        }
    }

    public Faculty editFaculty(Faculty faculty) {
        logger.info("Was invoked method for editFaculty");
        try {
            Faculty updated = facultyRepository.save(faculty);
            logger.debug("Updated faculty: {}", updated);
            return updated;
        } catch (Exception e) {
            logger.error("Error occurred while editing faculty", e);
            throw e;
        }
    }

    public void deleteFaculty(long id) {
        logger.info("Was invoked method for deleteFaculty with id={}", id);
        try {
            if (facultyRepository.existsById(id)) {
                facultyRepository.deleteById(id);
                logger.info("Deleted faculty with id={}", id);
            } else {
                logger.warn("Attempted to delete non-existent faculty with id={}", id);
            }
        } catch (Exception e) {
            logger.error("Error occurred while deleting faculty with id={}", id, e);
            throw e;
        }
    }

    public Collection<Faculty> getAllFaculties() {
        logger.info("Was invoked method for getAllFaculties");
        try {
            Collection<Faculty> faculties = facultyRepository.findAll();
            logger.debug("Fetched {} faculties", faculties.size());
            return faculties;
        } catch (Exception e) {
            logger.error("Error occurred while fetching all faculties", e);
            throw e;
        }
    }

    public Collection<Faculty> getFacultiesByColor(String color) {
        logger.info("Was invoked method for getFacultiesByColor with color={}", color);
        if (color == null || color.trim().isEmpty()) {
            logger.warn("Provided color is null or empty");
            return Collections.emptyList();
        }
        try {
            Collection<Faculty> faculties = facultyRepository.findByColor(color);
            logger.debug("Found {} faculties with color={}", faculties.size(), color);
            return faculties;
        } catch (Exception e) {
            logger.error("Error occurred while fetching faculties by color", e);
            throw e;
        }
    }

    public Collection<Faculty> searchFacultiesByNameOrColor(String query) {
        logger.info("Was invoked method for searchFacultiesByNameOrColor with query={}", query);
        try {
            Collection<Faculty> faculties = facultyRepository.findByNameOrColorIgnoreCase(query);
            logger.debug("Found {} faculties matching query={}", faculties.size(), query);
            return faculties;
        } catch (Exception e) {
            logger.error("Error occurred while searching faculties by name or color", e);
            throw e;
        }
    }

    public String getLongestFacultyName() {
        Collection<Faculty> faculties = getAllFaculties();
        return faculties.stream()
                .parallel()
                .map(Faculty::getName)
                .filter(name -> name != null && !name.isEmpty())
                .max(Comparator.comparingInt(String::length))
                .orElse(null);
    }
}