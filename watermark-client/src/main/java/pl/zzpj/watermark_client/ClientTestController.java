package pl.zzpj.watermark_client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/client")
public class ClientTestController {
    private final WatermarkFeignClient feignClient;

    public ClientTestController(WatermarkFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    public ResponseEntity<byte[]> sendToWatermarkService(
            @RequestParam("file") MultipartFile file,
            @RequestParam("message") String message) {

        return feignClient.applyWatermark(file, message);
    }
}
