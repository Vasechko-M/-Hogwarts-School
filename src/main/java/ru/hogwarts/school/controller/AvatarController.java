package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Tag(name = "Аватары студентов")
@RestController
@RequestMapping("/avatars")
public class AvatarController {
    private final AvatarService avatarService;
    private final StudentService studentService;

    public AvatarController(AvatarService avatarService, StudentService studentService) {
        this.avatarService = avatarService;
        this.studentService = studentService;
    }


    @Operation(summary = "Загрузка аватара")
    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable Long id,
                                               @RequestParam MultipartFile file) {
        try {
            avatarService.processAndUploadAvatar(id, file);
            return ResponseEntity.ok("Аватар успешно загружен");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка сохранения файла");
        }
    }


    @Operation(summary = "Показать аватар по id студента из базы данных")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getAvatarFromDb(@PathVariable Long studentId) {
        Avatar avatar = avatarService.findAvatar(studentId);
        if (avatar == null || avatar.getData() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Такого аватара или студента нет");
        }

        String mediaType = avatar.getMediaType();
        if (mediaType == null || mediaType.isEmpty()) {
            mediaType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .header("Content-Type", mediaType)
                .body(avatar.getData());
    }


    @Operation(summary = "Показать оригинал аватара по id студента")
    @GetMapping("/file/{studentId}")
    public ResponseEntity<?> getAvatarFromFile(@PathVariable Long studentId, HttpServletResponse response) {
        Avatar avatar = avatarService.findAvatar(studentId);
        if (avatar == null || avatar.getFilePath() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Такого аватара или студента нет");
        }
        try {
            Path path = Path.of(avatar.getFilePath());

            String mediaType = Files.probeContentType(path);
            response.setContentType(mediaType != null ? mediaType : "application/octet-stream");

            long fileSize = Files.size(path);
            if (fileSize > Integer.MAX_VALUE) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body("Файл слишком большой");
            }
            response.setContentLength((int) fileSize);

            try (InputStream is = Files.newInputStream(path);
                 OutputStream os = response.getOutputStream()) {
                is.transferTo(os);
            }
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Внутренняя ошибка сервера");
        }
    }
    @Operation(summary = "Получить список аватаров с пагинацией")
    @GetMapping
    public ResponseEntity<List<Avatar>> getAvatars(@RequestParam("page") Integer pageNumber,
                                                   @RequestParam("size") Integer pageSize) {

        List<Avatar> avatarList = avatarService.getAvatars(pageNumber, pageSize);
        return new ResponseEntity<>(avatarList, HttpStatus.OK);
    }
}


