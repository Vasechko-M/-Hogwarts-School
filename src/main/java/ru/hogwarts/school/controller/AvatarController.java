package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
                                               @RequestParam MultipartFile file) throws IOException{
        if (file.getSize() >= 2048 * 600) {
            return ResponseEntity.badRequest().body("File is too big");
        }
        avatarService.uploadAvatar(id, file);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Показать аватар по id студента")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<byte[]> getAvatarFromDb(@PathVariable Long studentId) {
        Avatar avatar = avatarService.findAvatar(studentId);
        if (avatar == null || avatar.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header("Content-Type", avatar.getMediaType())
                .body(avatar.getData());
    }


    @Operation(summary = "Показать оригинал аватара по id студента")
    @GetMapping("/file/{studentId}")
    public ResponseEntity<byte[]> getAvatarFromFile(@PathVariable Long studentId) {

        Avatar avatar = avatarService.findAvatar(studentId);
        if (avatar == null || avatar.getFilePath() == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            Path path = Path.of(avatar.getFilePath());
            byte[] bytes = Files.readAllBytes(path);
            String mediaType = Files.probeContentType(path);
            return ResponseEntity.ok()
                    .header("Content-Type", mediaType != null ? mediaType : "application/octet-stream")
                    .body(bytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}


