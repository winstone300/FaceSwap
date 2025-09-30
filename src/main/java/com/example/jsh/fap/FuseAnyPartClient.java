package com.example.jsh.fap;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class FuseAnyPartClient {

    private final FAPHttpProperties props;  // 서버주소 & 타임아웃 설정
    private final RestTemplate fapRestTemplate; // Apache 기반 RestTemplate

    //FastAPI 서버로 파일 전달
    public Path infer(MultipartFile target, MultipartFile eyes, MultipartFile nose, MultipartFile mouth,
                      String parts, int steps, double guidance) throws Exception {

        var baseUrl = props.getHttp().getBaseUrl();
        if (!StringUtils.hasText(baseUrl)) {
            throw new IllegalStateException("FastAPI 서버 주소가 설정되지 않았습니다. (fap.http.base-url)");
        }

        var url = baseUrl + "/infer";    //FastAPI에 보낼 url
        var body = new LinkedMultiValueMap<String, Object>();
        body.add("target", new InMemoryMultipart("target.png", target));
        if (eyes != null && !eyes.isEmpty())  body.add("eyes",  new InMemoryMultipart("eyes.png", eyes));
        if (nose != null && !nose.isEmpty())  body.add("nose",  new InMemoryMultipart("nose.png", nose));
        if (mouth != null && !mouth.isEmpty())body.add("mouth", new InMemoryMultipart("mouth.png", mouth));

        body.add("parts", parts == null || parts.isBlank() ? "eyes,nose,mouth" : parts);    // 바꿀 부위
        body.add("steps", String.valueOf(steps));                                           // 진행할 step
        body.add("guidance", String.valueOf(guidance));                                     // 조건 반영

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        var req = new HttpEntity<>(body, headers);
        ResponseEntity<byte[]> resp = fapRestTemplate.postForEntity(url, req, byte[].class);    // API요청 전송
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new RuntimeException("FAP inference failed: " + resp.getStatusCode());
        }
        //응답 저장
        Path out = Files.createTempFile("fap-result-", ".png");
        Files.write(out, resp.getBody());
        return out;
    }
}
