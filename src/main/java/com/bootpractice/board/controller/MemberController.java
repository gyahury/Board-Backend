package com.bootpractice.board.controller;

import com.bootpractice.board.domain.Member;
import com.bootpractice.board.dto.MemberJoinDto;
import com.bootpractice.board.dto.MemberLoginDto;
import com.bootpractice.board.dto.MemberUpdateDto;
import com.bootpractice.board.service.MemberService;
import com.bootpractice.board.utils.ResponseUtil;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = {"회원 생성/수정/삭제/조회"})
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    // 생성자 기반 의존성 주입
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinMember(@Valid @RequestBody MemberJoinDto memberDto, BindingResult result) {
        if (result.hasErrors()) {
            // 유효성 검사 에러의 첫번째 메세지 반환
            String firstErrorMessage = result.getAllErrors().get(0).getDefaultMessage();
            return ResponseUtil.badRequestMessage(firstErrorMessage);
        }
        memberService.saveMember(memberDto);
        return ResponseUtil.successMessage("join process is complete");
    }

    @GetMapping
    public ResponseEntity<?> getAllMembers() {
        return ResponseUtil.successMessage("getAllMembers process is complete",  memberService.findAllMembers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMemberById(@PathVariable Long id) {
        return ResponseUtil.successMessage("getMember process is complete",  memberService.findMemberById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(@PathVariable Long id, @Valid @RequestBody MemberUpdateDto memberUpdateDto,
                                          @RequestHeader("Authorization") String bearerToken) {
        memberUpdateDto.setId(id);
        return ResponseUtil.successMessage("update process is complete", memberService.updateMember(memberUpdateDto, bearerToken));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseUtil.successMessage("delete process is complete");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginMember(@RequestBody MemberLoginDto memberLoginDto){
        return ResponseUtil.successMessage("login process is complete", memberService.loginMember(memberLoginDto));
    }

}
