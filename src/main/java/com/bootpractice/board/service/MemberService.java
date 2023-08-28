package com.bootpractice.board.service;

import com.bootpractice.board.domain.Member;
import com.bootpractice.board.dto.MemberJoinDto;
import com.bootpractice.board.dto.MemberLoginDto;
import com.bootpractice.board.dto.MemberUpdateDto;
import com.bootpractice.board.exception.EmailAlreadyExistsException;
import com.bootpractice.board.exception.MemberNotFoundException;
import com.bootpractice.board.exception.UserMismatchException;
import com.bootpractice.board.repository.MemberRepository;
import com.bootpractice.board.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
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

    public Member saveMember(MemberJoinDto memberJoinDto) {
        Member member = memberJoinDto.toEntity();

        // 이메일 중복 체크
        Optional<Member> existingMember = memberRepository.findByEmail(member.getEmail());
        if (existingMember.isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        // 비밀번호 암호화
        String rawPassword = member.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        member.setPassword(encodedPassword);

        return memberRepository.save(member);
    }

    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }

    public Member findMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException());
    }

    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElse(null);
    }

    public String updateMember(MemberUpdateDto memberUpdateDto, String bearerToken) {

        String token = bearerToken.split(" ")[1];
        String tokenEmail = JwtUtil.getEmail(token, secretkey);

        // 회원 존재 여부 체크
        Member existingMember = memberRepository.findById(memberUpdateDto.getId()).orElseThrow(() -> new MemberNotFoundException());

        // 이메일 중복 여부 체크
        Optional<Member> existingEmailMember = memberRepository.findByEmail(memberUpdateDto.getEmail());
        if (existingEmailMember.isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        // 토큰 이메일과 업데이트될 이메일 비교
        if(tokenEmail.equals(existingMember.getEmail())){
            existingMember.setEmail(memberUpdateDto.getEmail());
            existingMember.setUsername(memberUpdateDto.getUsername());
            existingMember.setNickname(memberUpdateDto.getNickname());

            // 비밀번호 암호화
            String rawPassword = memberUpdateDto.getPassword();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            existingMember.setPassword(encodedPassword);

            memberRepository.save(existingMember);

            return JwtUtil.createJwt(existingMember.getEmail(), existingMember.getUsername(), secretkey, expiredMs);
        }else{
            throw new UserMismatchException();
        }

    }

    public void deleteMember(Long id) {
        memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException());
        memberRepository.deleteById(id);
    }

    @Value("${jwt.secret}")
    private String secretkey;

    private final Long expiredMs = 1000 * 60 * 60L; // 60분

    public String loginMember(MemberLoginDto memberLoginDto){

        Member existingMember = memberRepository.findByEmail(memberLoginDto.getEmail()).orElseThrow(() -> new MemberNotFoundException());

        if (passwordEncoder.matches(memberLoginDto.getPassword(), existingMember.getPassword())) {
            return JwtUtil.createJwt(existingMember.getEmail(), existingMember.getUsername(), secretkey, expiredMs);
        } else {
            throw new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

    }
}
