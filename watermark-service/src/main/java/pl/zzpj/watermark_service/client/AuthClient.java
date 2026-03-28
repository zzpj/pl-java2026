package pl.zzpj.watermark_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-server")
public interface AuthClient {

    @PostMapping("/auth/validate")
    boolean validateToken(@RequestParam("token") String token);
}