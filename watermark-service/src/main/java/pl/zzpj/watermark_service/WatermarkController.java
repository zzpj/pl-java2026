package pl.zzpj.watermark_service;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public class WatermarkController {
    private final SteganographyService steganographyService;

    public WatermarkController(SteganographyService steganographyService) {
        this.steganographyService = steganographyService;
    }

    public ResponseEntity<byte[]> applyWatermark(
            @RequestParam("file") MultipartFile file,
            @RequestParam("message") String message
            ) {

        byte[] watermarkedImage = steganographyService.embedMessage(file, message);

        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(watermarkedImage);
    }
}
