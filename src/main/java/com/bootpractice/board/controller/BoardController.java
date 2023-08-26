package com.bootpractice.board.controller;

import com.bootpractice.board.domain.Board;
import com.bootpractice.board.domain.Member;
import com.bootpractice.board.dto.BoardCreateDto;
import com.bootpractice.board.service.BoardService;
import com.bootpractice.board.service.MemberService;
import com.bootpractice.board.utils.JwtUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api(tags = {"게시글 생성/수정/삭제/조회"})
@RequestMapping("/api/boards")
public class BoardController {
    private final BoardService boardService;
    private final MemberService memberService;

    // 생성자 기반 의존성 주입
    public BoardController(BoardService boardService, MemberService memberService) {
        this.boardService = boardService;
        this.memberService = memberService;
    }

    @Value("${jwt.secret}")
    private String secretkey;

    @PostMapping("/create")
    public ResponseEntity<?> createBoard(@Valid @RequestBody BoardCreateDto boardCreateDto, Member member, BindingResult result,
                                         @RequestHeader("Authorization") String bearerToken){

        if (result.hasErrors()) {
            // 유효성 검사 에러의 첫번째 메세지 반환
            String firstErrorMessage = result.getAllErrors().get(0).getDefaultMessage();

            return ResponseEntity.badRequest()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
                    .body(firstErrorMessage);
        }

        String token = bearerToken.split(" ")[1];

        String email = JwtUtil.getEmail(token, secretkey);

        member = memberService.findMemberByEmail(email);

        Board createBoard = boardService.createBoard(boardCreateDto, member);

        System.out.println(createBoard);

        return ResponseEntity.ok(createBoard);
    }


}
