package com.bootpractice.board.utils;

import com.bootpractice.board.domain.Member;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseUtil {
    public static ResponseEntity<Map<String, Object>> successMessage(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", "success");
        return ResponseEntity.ok().body(response);
    }

    public static ResponseEntity<Map<String, Object>> successMessage(String message, String token) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", "success");
        if (token != null) {
            response.put("token", token);
        }
        return ResponseEntity.ok().body(response);
    }

    public static ResponseEntity<Map<String, Object>> successMessage(String message, Member member) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", "success");
        response.put("data", member);
        return ResponseEntity.ok().body(response);
    }

    public static ResponseEntity<Map<String, Object>> successMessage(String message, List<Member> memberList) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", "success");
        response.put("data", memberList);
        return ResponseEntity.ok().body(response);
    }

    public static ResponseEntity<Map<String, Object>> badRequestMessage(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", "error");
        return ResponseEntity.badRequest().body(response);
    }

}
