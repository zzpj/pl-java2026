package pl.zzpj.watermark_service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class SteganographyService {
    public byte[] embedMessage(MultipartFile file, String message) {
        try {
            byte[] originalImageBytes = file.getBytes();

            return originalImageBytes;
        } catch (IOException e) {
            throw new RuntimeException("An error occurred", e);
        }
    }
}
