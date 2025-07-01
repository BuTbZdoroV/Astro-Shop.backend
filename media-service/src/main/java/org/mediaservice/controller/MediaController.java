package org.mediaservice.controller;

import lombok.RequiredArgsConstructor;
import org.mediaservice.service.MediaService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        return mediaService.uploadImage(file);
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
        return mediaService.serveFile(fileName);
    }

    @GetMapping("/getListProfileBackgrounds")
    public ResponseEntity<?> getListProfileBackgrounds() {
        return mediaService.getListProfileBackgrounds();
    }

}
