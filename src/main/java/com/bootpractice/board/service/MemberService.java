package com.bootpractice.board.service;

import com.bootpractice.board.domain.Member;
import com.bootpractice.board.dto.MemberJoinDto;
import com.bootpractice.board.exception.EmailAlreadyExistsException;
import com.bootpractice.board.exception.MemberNotFoundException;
import com.bootpractice.board.repository.MemberRepository;
import com.bootpractice.board.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    // 생성자 주입
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member saveMember(MemberJoinDto memberDto) {
        Member member = memberDto.toEntity();

        // 비밀번호 암호화
        String rawPassword = member.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        member.setPassword(encodedPassword);

        Optional<Member> existingMember = memberRepository.findByEmail(member.getEmail());

        // 이메일 중복 체크
        if (existingMember.isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        return memberRepository.save(member);
    }

    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }

    public Member findMemberById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    public Member updateMember(Member member) {

        Optional<Member> existingMember = memberRepository.findById(member.getId());
        if (existingMember.isPresent()) {
            Member updateMember = existingMember.get();
            updateMember.setEmail(member.getEmail());
            updateMember.setUsername(member.getUsername());
            updateMember.setNickname(member.getNickname());
            updateMember.setPassword(member.getPassword());
            return memberRepository.save(updateMember);
        } else {
            throw new MemberNotFoundException();
        }


    }

    public void deleteMember(Long id) {
        try {
            memberRepository.deleteById(id);
        } catch(EmptyResultDataAccessException e) {
            throw new MemberNotFoundException();
        }
    }

    @Value("${jwt.secret}")
    private String secretkey;

    private Long expiredMs = 1000 * 60 * 60l; // 60분

    public String loginMember(MemberJoinDto memberJoinDto){

        // 인증과정 작성

        return JwtUtil.createJwt(memberJoinDto.getUsername(), secretkey, expiredMs);
    }
}
