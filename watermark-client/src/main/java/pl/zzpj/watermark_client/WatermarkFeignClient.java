package pl.zzpj.watermark_client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "watermark-service")
public interface WatermarkFeignClient {

    @PostMapping(value = "/api/watermark", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<byte[]> applyWatermark(
            @RequestPart("file") MultipartFile file,
            @RequestPart("message") String message
            );
}
