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
import org.springframework.web.bind.annotation.RequestHeader;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberService {

    @Value("${jwt.secret}")
    private String secretkey;
    private final Long expiredMs = 1000 * 60 * 60L; // 60분
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveMember(MemberJoinDto memberJoinDto) {
        Member member = memberJoinDto.toEntity();

        Optional<Member> existingMember = memberRepository.findByEmail(member.getEmail());
        if (existingMember.isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        String rawPassword = member.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        member.setPassword(encodedPassword);

        memberRepository.save(member);
    }

    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }

    public Member findMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException());
    }

    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new MemberNotFoundException());
    }

    public String updateMember(MemberUpdateDto memberUpdateDto, String bearerToken) {

        String token = bearerToken.split(" ")[1];
        String tokenEmail = JwtUtil.getEmail(token, secretkey);

        Member member = memberRepository.findById(memberUpdateDto.getId()).orElseThrow(() -> new MemberNotFoundException());

        Optional<Member> existingEmail = memberRepository.findByEmail(memberUpdateDto.getEmail());
        if (existingEmail.isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        if(tokenEmail.equals(member.getEmail())){
            member.setEmail(memberUpdateDto.getEmail());
            member.setUsername(memberUpdateDto.getUsername());
            member.setNickname(memberUpdateDto.getNickname());

            String rawPassword = memberUpdateDto.getPassword();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            member.setPassword(encodedPassword);

            memberRepository.save(member);

            return JwtUtil.createJwt(member.getEmail(), member.getUsername(), secretkey, expiredMs);
        }else{
            throw new UserMismatchException();
        }

    }

    public void deleteMember(Long id) {
        memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException());
        memberRepository.deleteById(id);
    }

    public String loginMember(MemberLoginDto memberLoginDto){

        Member existingMember = memberRepository.findByEmail(memberLoginDto.getEmail()).orElseThrow(() -> new MemberNotFoundException());

        if (passwordEncoder.matches(memberLoginDto.getPassword(), existingMember.getPassword())) {
            return JwtUtil.createJwt(existingMember.getEmail(), existingMember.getUsername(), secretkey, expiredMs);
        } else {
            throw new BadCredentialsException("email or password is incorrect");
        }

    }
}
