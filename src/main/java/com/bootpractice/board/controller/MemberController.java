package com.bootpractice.board.controller;

import com.bootpractice.board.domain.Member;
import com.bootpractice.board.dto.MemberJoinDto;
import com.bootpractice.board.service.MemberService;
import io.swagger.annotations.Api;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

            return ResponseEntity.badRequest()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
                    .body(firstErrorMessage);
        }

        Member joinMember = memberService.saveMember(memberDto);

        return ResponseEntity.ok(joinMember);
    }

    @GetMapping
    public List<Member> getAllMembers() {
        return memberService.findAllMembers();
    }

    @GetMapping("/{id}")
    public Member getMemberById(@PathVariable Long id) {
        return memberService.findMemberById(id);
    }

    @PutMapping("/{id}")
    public Member updateMember(@PathVariable Long id, @RequestBody Member member) {
        member.setId(id);
        return memberService.updateMember(member);
    }

    @DeleteMapping("/{id}")
    public void deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginMember(@RequestBody MemberJoinDto memberDto){
        return ResponseEntity.ok().body(memberService.loginMember(memberDto));
    }


}
