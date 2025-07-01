package org.mediaservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MediaService {

    @Value("${app.upload.dir}")
    private String uploadDir;


    /**
     * Загружает файл на сервер и сохраняет его в указанной директории.
     * Генерирует уникальное имя файла для избежания коллизий.
     *
     * @param file Файл, переданный через HTTP-запрос (например, изображение).
     * @return ResponseEntity с именем сохраненного файла в формате JSON (ключ "fileName")
     *         или сообщением об ошибке.
     * @throws IOException Если произошла ошибка при создании директории или записи файла.
     *
     * @Example Пример ответа:
     * <pre>
     * {
     *   "fileName": "a1b2c3d4_photo.jpg"
     * }
     * </pre>
     */
    public ResponseEntity<?> uploadImage(MultipartFile file) {
        try {
            // Создаем директорию, если не существует

            long maxSize = 5 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("File size exceeds 5MB limit");
            }

            // Проверка типа файла
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Only image files are allowed");
            }

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Генерируем уникальное имя файла
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            // Возвращаем имя файла, а не полный путь
            Map<String, String> response = new HashMap<>();
            response.put("fileName", fileName);
            return ResponseEntity.ok().body(response);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
        }
    }

    /**
     * Отдает файл клиенту по его имени. Определяет MIME-тип автоматически.
     *
     * @param fileName Имя файла (например, "a1b2c3d4_photo.jpg").
     * @return ResponseEntity с содержимым файла и корректными HTTP-заголовками:
     *         - Content-Type (на основе MIME-типа)
     *         - Content-Disposition (inline для отображения в браузере)
     * @throws MalformedURLException Если путь к файлу некорректен.
     * @throws IOException Если файл недоступен для чтения.
     *
     * @HTTP коды ответов:
     * - 200 OK: Файл найден и успешно отправлен.
     * - 404 Not Found: Файл не существует или недоступен.
     * - 400 Bad Request: Некорректное имя файла.
     * - 500 Internal Server Error: Ошибка при чтении файла.
     */
    public ResponseEntity<Resource> serveFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<?> getListProfileBackgrounds() {
        record BackgroundDTO(Long id, String url) {}

        List<BackgroundDTO> backgrounds = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            backgrounds.add(new BackgroundDTO((long) i, "public/assets/profile/backgrounds/bg" + i + ".jpg"));
        }

        return ResponseEntity.ok(backgrounds);
    }

}
