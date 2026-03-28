package pl.zzpj.watermark_service.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.zzpj.watermark_service.service.SteganographyService;

@RestController
@RequestMapping("/watermark")
@RequiredArgsConstructor
public class WatermarkController {

    private final SteganographyService steganographyService;

    @GetMapping("/test")
    public ResponseEntity<String> testSteganography() {
        String result = steganographyService.doSomething();
        return ResponseEntity.ok(result);
    }
}
