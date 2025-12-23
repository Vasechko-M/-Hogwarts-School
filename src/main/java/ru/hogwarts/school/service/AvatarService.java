package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.AvatarRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class AvatarService {

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    @Value("${students.avatar.dir.path}")
    private String avatarsDir;

    private final StudentService studentService;
    private final AvatarRepository avatarRepository;

    public AvatarService(StudentService studentService, AvatarRepository avatarRepository) {
        this.studentService = studentService;
        this.avatarRepository = avatarRepository;
    }

    public void processAndUploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Was invoked method for processAndUploadAvatar");
        logger.debug("Processing upload for studentId={} with file size={}", studentId, file.getSize());
        if (file.getSize() >= 2048 * 600) {
            logger.warn("File size exceeds limit: size={}", file.getSize());
            throw new IllegalArgumentException("Файл очень большой");
        }
        Student student;
        try {
            student = studentService.findStudent(studentId);
            logger.debug("Found student: {}", student);
        } catch (Exception e) {
            logger.error("Error finding student with ID={}", studentId, e);
            throw e;
        }
        uploadAvatar(studentId, file);
    }

    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Was invoked method for uploadAvatar");
        Student student;
        try {
            student = studentService.findStudent(studentId);
            logger.debug("Found student: {}", student);
        } catch (Exception e) {
            logger.error("Error finding student with ID={}", studentId, e);
            throw e;
        }

        try {
            Path filePath = Path.of(avatarsDir, studentId + "." + getExtension(file.getOriginalFilename()));
            Files.createDirectories(filePath.getParent());
            Files.deleteIfExists(filePath);

            try (InputStream is = file.getInputStream();
                 OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                 BufferedInputStream bis = new BufferedInputStream(is, 1024);
                 BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
            ) {
                logger.debug("Starting transfer of input stream to output stream");
                bis.transferTo(bos);
            }

            Avatar avatar = findAvatar(studentId);
            avatar.setStudent(student);
            avatar.setFilePath(filePath.toString());
            avatar.setFileSize(file.getSize());
            avatar.setMediaType(file.getContentType());
            avatar.setData(generateImageData(filePath));

            avatarRepository.save(avatar);
            logger.info("Avatar uploaded successfully for studentId={}", studentId);
        } catch (IOException e) {
            logger.error("Error during uploading avatar for studentId={}", studentId, e);
            throw e;
        }
    }

    public Avatar findAvatar(Long studentId) {
        logger.info("Was invoked method for findAvatar");
        try {
            return avatarRepository.findByStudentId(studentId).orElseGet(() -> {
                logger.warn("No avatar found for studentId={}", studentId);
                return new Avatar();
            });
        } catch (Exception e) {
            logger.error("Error fetching avatar for studentId={}", studentId, e);
            throw e;
        }
    }

    private String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        String ext = "png";
        if (lastDotIndex != -1 && lastDotIndex != fileName.length() - 1) {
            ext = fileName.substring(lastDotIndex + 1);
        }
        logger.debug("Determined file extension: {}", ext);
        return ext;
    }

    private byte[] generateImageData(Path filePath) throws IOException {
        logger.info("Was invoked method for generateImageData");
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);
            if (image == null) {
                logger.error("Failed to read image from path: {}", filePath);
                throw new IOException("Не удалось прочитать изображение");
            }

            int width = 100;
            int height = (image.getHeight() * width) / image.getWidth();
            if (image.getWidth() == 0) {
                height = width;
            }

            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();

            logger.debug("Writing resized image to byte array");
            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }

    public java.util.List<Avatar> getAvatars(Integer pageNumber, Integer pageSize) {
        logger.info("Was invoked method for getAvatars");
        logger.debug("Fetching page {} with size {}", pageNumber, pageSize);
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return avatarRepository.findAll(pageRequest).getContent();
    }
}