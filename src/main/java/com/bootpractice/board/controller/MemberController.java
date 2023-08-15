package com.bootpractice.board.controller;

import com.bootpractice.board.domain.Member;
import com.bootpractice.board.service.MemberService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Api(tags = {"회원 조회/생성"})
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    // 생성자 기반 의존성 주입
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public Member createMember(@RequestBody Member member) {
        return memberService.saveMember(member);
    }

    @GetMapping
    public List<Member> getAllMembers() {
        return memberService.findAllMembers();
    }

}
