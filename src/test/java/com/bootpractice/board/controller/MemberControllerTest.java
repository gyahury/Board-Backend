package com.bootpractice.board.controller;

import com.bootpractice.board.config.SecurityConfig;
import com.bootpractice.board.domain.Member;
import com.bootpractice.board.dto.MemberJoinDto;
import com.bootpractice.board.dto.MemberLoginDto;
import com.bootpractice.board.dto.MemberUpdateDto;
import com.bootpractice.board.service.MemberService;
import com.bootpractice.board.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
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

    @Value("${jwt.secret}")
    private String secretkey;
    private final Long expiredMs = 1000 * 60 * 60L; // 60분

    @Test
    @DisplayName("모든 필드가 유효한 경우 회원 가입 성공과 함께 200 OK 반환")
    public void testJoinMember() throws Exception {
        MemberJoinDto memberJoinDto = new MemberJoinDto();

        memberJoinDto.setEmail("testEmail@gmail.com");
        memberJoinDto.setNickname("testNickname");
        memberJoinDto.setUsername("testUsername");
        memberJoinDto.setPassword("testPassword");

        // 동작을 지정
        doNothing().when(memberService).saveMember(any(MemberJoinDto.class));

        mockMvc.perform(post("/api/member/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberJoinDto)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("success"))
                        .andExpect(jsonPath("$.message").value("member/join process is complete"));
    }

    @Test
    @DisplayName("잘못된 형식의 이메일로 회원 가입 실패와 함께 400 BadRequest 반환")
    public void testInvalidEmailJoinMember() throws Exception {
        MemberJoinDto memberJoinDto = new MemberJoinDto();

        memberJoinDto.setEmail("testEmail.com");
        memberJoinDto.setNickname("testNickname");
        memberJoinDto.setUsername("testUsername");
        memberJoinDto.setPassword("testPassword");

        doNothing().when(memberService).saveMember(any(MemberJoinDto.class));


        mockMvc.perform(post("/api/member/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberJoinDto)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.status").value("error"))
                        .andExpect(jsonPath("$.message").value("It must be in a valid email format"));
    }

    @Test
    @DisplayName("비밀번호가 8자 이상 입력되지 않은 경우 회원 가입 실패와 함께 400 BadRequest 반환")
    public void testRequiredEmailJoinMember() throws Exception {
        MemberJoinDto memberJoinDto = new MemberJoinDto();

        memberJoinDto.setEmail("testEmail@gmail.com");
        memberJoinDto.setNickname("testNickname");
        memberJoinDto.setUsername("testUsername");
        memberJoinDto.setPassword("1234567");

        doNothing().when(memberService).saveMember(any(MemberJoinDto.class));


        mockMvc.perform(post("/api/member/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberJoinDto)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.status").value("error"))
                        .andExpect(jsonPath("$.message").value("password must be at least 8 characters"));
    }

    @Test
    @WithMockUser
    @DisplayName("전체 회원 조회시 성공과 함께 200 OK 반환")
    public void testGetAllMembers() throws Exception {

        mockMvc.perform(get("/api/member"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("success"))
                        .andExpect(jsonPath("$.message").value("getAllMembers process is complete"));
    }

    @Test
    @WithMockUser
    @DisplayName("id로 회원 조회시 성공과 함께 200 OK 반환")
    public void testGetMemberById() throws Exception {

        Member mockMember = new Member();

        Long memberId = 1L;
        mockMember.setId(memberId);
        mockMember.setEmail("testEmail@gmail.com");
        mockMember.setNickname("testNickname");
        mockMember.setUsername("testUsername");
        mockMember.setPassword("testPassword");

        when(memberService.findMemberById(memberId)).thenReturn(mockMember);

        mockMvc.perform(get("/api/member/"+memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("getMember process is complete"))
                .andExpect(jsonPath("$.data.id").value(memberId))
                .andExpect(jsonPath("$.data.email").value("testEmail@gmail.com"))
                .andExpect(jsonPath("$.data.nickname").value("testNickname"))
                .andExpect(jsonPath("$.data.username").value("testUsername"))
                .andExpect(jsonPath("$.data.password").value("testPassword"));
    }

    @Test
    @WithMockUser
    @DisplayName("유효한 회원 정보를 업데이트하면 성공과 함께 200 OK 반환")
    public void testUpdateMember() throws Exception {
        MemberUpdateDto memberUpdateDto = new MemberUpdateDto();
        memberUpdateDto.setEmail("testModifiedEmail@gmail.com");
        memberUpdateDto.setNickname("testModifiedNickname");
        memberUpdateDto.setUsername("testModifiedUsername");
        memberUpdateDto.setPassword("testModifiedPassword");

        Member mockMember = new Member();
        mockMember.setEmail("testEmail@gmail.com");
        mockMember.setUsername("testUsername");

        // 테스트용 토큰 생성
        String mockToken = "Bearer " + JwtUtil.createJwt(mockMember.getEmail(), mockMember.getUsername(), secretkey, expiredMs); // assuming 1 hour expiration

        // 서비스 기능 모킹
        when(memberService.updateMember( any(MemberUpdateDto.class), anyString() )).thenReturn("updatedToken");

        Long memberId = 1L;

        mockMvc.perform(put("/api/member/"+memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", mockToken)
                        .content(objectMapper.writeValueAsString(memberUpdateDto)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("success"))
                        .andExpect(jsonPath("$.message").value("member/update process is complete"))
                        .andExpect(jsonPath("$.token").value("updatedToken"));
    }

    @Test
    @WithMockUser
    @DisplayName("id를 통해 회원 삭제시 성공과 함께 200 OK 반환")
    public void testDeleteMember() throws Exception {

        Long mockMemberId = 1L;

        doNothing().when(memberService).deleteMember(mockMemberId);

        mockMvc.perform(delete("/api/member/"+mockMemberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("member/delete process is complete"));

    }

    @Test
    @DisplayName("유효한 회원 정보로 로그인하는 경우 성공과 함께 200 OK 반환")
    public void testLoginMember() throws Exception {
        MemberLoginDto memberLoginDto = new MemberLoginDto();

        memberLoginDto.setEmail("testEmail@gmail.com");
        memberLoginDto.setPassword("testPassword");

        // 서비스 기능 모킹
        when(memberService.loginMember( memberLoginDto )).thenReturn("loginSuccessToken");

        mockMvc.perform(post("/api/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberLoginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("member/login process is complete"))
                .andExpect(jsonPath("$.token").value("loginSuccessToken"));

    }

}
