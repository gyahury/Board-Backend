package com.bootpractice.board.controller;

import com.bootpractice.board.domain.Board;
import com.bootpractice.board.domain.Member;
import com.bootpractice.board.dto.BoardCreateDto;
import com.bootpractice.board.exception.BoardNotFoundException;
import com.bootpractice.board.service.BoardService;
import com.bootpractice.board.service.MemberService;
import com.bootpractice.board.utils.JwtUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


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

        return ResponseEntity.ok(createBoard);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getBoards(@PageableDefault(sort = "id") Pageable pageable) {
        Page<Board> boards = boardService.getBoards(pageable);

        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoard(@PathVariable Long id) {
        Optional<Board> boardOptional = boardService.getBoardById(id);

        // 추후 리팩토링 예정(service단에서 exception 처리로)
        if (boardOptional.isPresent()) {
            return ResponseEntity.ok(boardOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable Long id, @RequestBody Board updatedBoard) {
        try {
            Board board = boardService.updateBoard(id, updatedBoard);
            return new ResponseEntity<>(board, HttpStatus.OK);
        } catch (BoardNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBoard(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            boardService.deleteBoard(id);
            response.put("message", "삭제가 완료되었습니다.");
            return ResponseEntity.ok(response);
        } catch (EmptyResultDataAccessException e) {
            response.put("message", "해당 게시글이 존재하지 않습니다.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "서버 오류입니다.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
