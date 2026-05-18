package ru.hogwarts.school.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.school.Avatar;
import ru.hogwarts.school.model.school.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarService {

    private static final Logger log = LoggerFactory.getLogger(AvatarService.class);
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Value("${files.student.uploads.avatar}")
    private String avatarDirPath;

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    @PostConstruct
    public void initDirs() {
        try {
            Files.createDirectories(Path.of(avatarDirPath));
        } catch (IOException e) {
            throw new RuntimeException("Could not create avatar directory: %s".formatted(avatarDirPath));
        }
    }

    public Long uploadAvatar(Long studentId, MultipartFile file) {
        try {
            Student student = studentRepository.findById(studentId).orElseThrow(
                    () -> new NoSuchElementException("Cannot find student with id: %s".formatted(studentId)));

            Path filepath = Path.of(avatarDirPath, studentId + "." + getExtensions(Objects.requireNonNull(file.getOriginalFilename())));

            saveFileToDisk(file, filepath);

            Avatar avatar = Objects.requireNonNullElse(student.getAvatar(), new Avatar());
            if (avatar.getStudent() == null) {
                avatar.setStudent(student);
            }

            avatar.setFilePath(filepath.toString());
            avatar.setFileSize(file.getSize());
            avatar.setMediaType(file.getContentType());

            avatar.setData(generateImagePreview(filepath));

            return avatarRepository.save(avatar).getId();

        } catch (IOException e) {
            throw new UncheckedIOException("Ошибка при загрузке аватара студента: %s".formatted(studentId), e);
        }
    }

    private void saveFileToDisk(MultipartFile file, Path filepath) throws IOException {
        Files.deleteIfExists(filepath);
        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filepath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {
            bis.transferTo(bos);
        }
    }

    public Avatar findAvatar(Long studentId) {
        return avatarRepository.findByStudentId(studentId).orElseThrow(
                () -> new NoSuchElementException("Avatar for student %s not found".formatted(studentId)));
    }

    public Collection<Avatar> findAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page-1, size);
        return avatarRepository.findAll(pageRequest).getContent();
    }

    private byte[] generateImagePreview(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024)) {
            BufferedImage image = ImageIO.read(bis);

            int height = image.getHeight() / (image.getWidth() / 100);

            int imageType = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();

            BufferedImage preview = new BufferedImage(100, height, imageType);
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(preview, getExtensions(filePath.getFileName().toString()), baos);
                return baos.toByteArray();
            }
        }
    }

    private String getExtensions(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @Transactional
    public void deleteAvatar(Long studentId) {

        Avatar avatar = avatarRepository.findByStudentId(studentId).orElseThrow(
                () -> new NoSuchElementException("Avatar for student id: %s not found".formatted(studentId))
        );

        String filePath = avatar.getFilePath();

        avatarRepository.delete(avatar);

        try {
            Files.deleteIfExists(Path.of(filePath));
        } catch (IOException e) {
            log.error("Cannot delete avatar file: {}, {}", filePath, e.getMessage());
        }


    }
}