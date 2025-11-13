package ru.hogwarts.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.hogwarts.school.model.Faculty;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    List<Faculty> findByColor(String color);

    @Query("SELECT f FROM Faculty f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(f.color) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Faculty> findByNameOrColorIgnoreCase(@Param("query") String query);
}
