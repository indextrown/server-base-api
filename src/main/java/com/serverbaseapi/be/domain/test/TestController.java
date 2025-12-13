package com.serverbaseapi.be.domain.test;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    // ----- 테스트 -----
    @Operation(summary = "[Test] 테스트"
            ,description = "테스트 코드 입니다.",
            tags = {"[Test] 테스트"}
    )
    @GetMapping("/test")
    public Map<String, String> test(){
        return Map.of("message", "test");
    }

    // ----- 메모 리스트 샘플 (DTO 없이) -----
    @Operation(
            summary = "[Test] 메모 리스트 샘플",
            description = "iOS Memo 모델 디코딩 테스트용 입니다. (DTO 없이 Map으로 바로 반환)",
            tags = {"[Test] 테스트"}
    )
    @GetMapping("/memos")
    public ResponseEntity<List<Map<String, String>>> getMemos() {

        List<Map<String, String>> memos = List.of(
                Map.of(
                        "id", "1",
                        "createAt", "2025-12-13T10:00:00",
                        "title", "첫 번째 메모",
                        "viewCount", "10"
                ),
                Map.of(
                        "id", "2",
                        "createAt", "2025-12-13T11:00:00",
                        "title", "두 번째 메모",
                        "viewCount", "21"
                ),
                Map.of(
                        "id", "3",
                        "createAt", "2025-12-13T12:00:00",
                        "title", "세 번째 메모",
                        "viewCount", "7"
                ),
                Map.of(
                        "id", "4",
                        "createAt", "2025-12-13T13:00:00",
                        "title", "네 번째 메모",
                        "viewCount", "3"
                ),
                Map.of(
                        "id", "5",
                        "createAt", "2025-12-13T14:00:00",
                        "title", "다섯 번째 메모",
                        "viewCount", "42"
                )
        );

        return ResponseEntity.ok(memos);
    }

    // ----- 메모 단일 조회 -----
    @Operation(
            summary = "[Test] 메모 단일 샘플",
            description = "단일 아이템을 반환합니다.",
            tags = {"[Test] 테스트"}
    )
    @GetMapping("/memos/{id}")
    public ResponseEntity<Map<String, String>> getMemo(@PathVariable String id) {

        // 샘플 데이터 (실제로는 DB 조회)
        List<Map<String, String>> memos = List.of(
                Map.of("id", "1", "createAt", "2025-12-13T10:00:00", "title", "첫 번째 메모", "viewCount", "10"),
                Map.of("id", "2", "createAt", "2025-12-13T11:00:00", "title", "두 번째 메모", "viewCount", "21"),
                Map.of("id", "3", "createAt", "2025-12-13T12:00:00", "title", "세 번째 메모", "viewCount", "7"),
                Map.of("id", "4", "createAt", "2025-12-13T13:00:00", "title", "네 번째 메모", "viewCount", "3"),
                Map.of("id", "5", "createAt", "2025-12-13T14:00:00", "title", "다섯 번째 메모", "viewCount", "42")
        );

        return memos.stream()
                .filter(memo -> memo.get("id").equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
