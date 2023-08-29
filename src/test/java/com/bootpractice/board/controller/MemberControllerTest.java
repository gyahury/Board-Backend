package com.bootpractice.board.controller;

import com.bootpractice.board.config.SecurityConfig;
import com.bootpractice.board.dto.MemberJoinDto;
import com.bootpractice.board.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = {MemberController.class, SecurityConfig.class})
@DisplayName("MemberController 테스트")
public class MemberControllerTest {

    @MockBean
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("모든 필드가 유효할 때 회원가입 성공과 함께 200 OK 반환")
    public void testJoinMember() throws Exception {
        MemberJoinDto memberJoinDto = new MemberJoinDto();

        memberJoinDto.setEmail("testEmail@gmail.com");
        memberJoinDto.setNickname("testNickname");
        memberJoinDto.setUsername("testUsername");
        memberJoinDto.setPassword("testPassword");

        // 동작을 지정
        when(memberService.saveMember(any(MemberJoinDto.class))).thenReturn(null);


        mockMvc.perform(post("/api/member/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberJoinDto)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("success"))
                        .andExpect(jsonPath("$.message").value("member/join process is complete"));
    }

    @Test
    @DisplayName("잘못된 형식의 이메일로 회원가입 실패와 함께 400 BadRequest 반환")
    public void testInvalidEmailJoinMember() throws Exception {
        MemberJoinDto memberJoinDto = new MemberJoinDto();

        memberJoinDto.setEmail("testEmail.com");
        memberJoinDto.setNickname("testNickname");
        memberJoinDto.setUsername("testUsername");
        memberJoinDto.setPassword("testPassword");

        when(memberService.saveMember(any(MemberJoinDto.class))).thenReturn(null);


        mockMvc.perform(post("/api/member/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberJoinDto)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.status").value("error"))
                        .andExpect(jsonPath("$.message").value("It must be in a valid email format"));
    }

    @Test
    @DisplayName("비밀번호가 8자 이상 입력되지 않은 경우 회원가입 실패와 함께 400 BadRequest 반환")
    public void testRequiredEmailJoinMember() throws Exception {
        MemberJoinDto memberJoinDto = new MemberJoinDto();

        memberJoinDto.setEmail("testEmail@gmail.com");
        memberJoinDto.setNickname("testNickname");
        memberJoinDto.setUsername("testUsername");
        memberJoinDto.setPassword("1234567");

        when(memberService.saveMember(any(MemberJoinDto.class))).thenReturn(null);


        mockMvc.perform(post("/api/member/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberJoinDto)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.status").value("error"))
                        .andExpect(jsonPath("$.message").value("password must be at least 8 characters"));
    }

    @Test
    @WithMockUser
    @DisplayName("인가 후 전체 회원 조회시 성공과 함께 200 OK 반환")
    public void testGetAllMembers() throws Exception {

        mockMvc.perform(get("/api/member"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("success"))
                        .andExpect(jsonPath("$.message").value("getAllMembers process is complete"));
    }

}
