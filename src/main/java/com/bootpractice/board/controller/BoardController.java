package com.bootpractice.board.controller;

import com.bootpractice.board.domain.Board;
import com.bootpractice.board.domain.Member;
import com.bootpractice.board.dto.BoardCreateDto;
import com.bootpractice.board.dto.BoardUpdateDto;
import com.bootpractice.board.service.BoardService;
import com.bootpractice.board.utils.ResponseUtil;
import io.swagger.annotations.Api;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@Api(tags = {"게시글 생성/수정/삭제/조회"})
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBoard(@Valid @RequestBody BoardCreateDto boardCreateDto, BindingResult result,
                                         @RequestHeader("Authorization") String bearerToken){

        if (result.hasErrors()) {
            String firstErrorMessage = result.getAllErrors().get(0).getDefaultMessage();
            return ResponseUtil.badRequestMessage(firstErrorMessage);
        }

        boardService.createBoard(boardCreateDto, bearerToken);
        return ResponseUtil.successMessage("board/create process is complete");
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllBoards(@PageableDefault(sort = "id") Pageable pageable) {
        Page<Board> boardList = boardService.getAllBoards(pageable);
        return ResponseUtil.successMessage("getAllBoards process is complete", boardList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBoardById(@PathVariable Long id) {
        Board board = boardService.getBoardById(id);
        return ResponseUtil.successMessage("getBoard process is complete", board);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBoard(@PathVariable Long id, @RequestBody BoardUpdateDto boardUpdateDto,
                                         @RequestHeader("Authorization") String bearerToken) {
        boardService.updateBoard(id, boardUpdateDto, bearerToken);
        return ResponseUtil.successMessage("board/update process is complete");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long id, @RequestHeader("Authorization") String bearerToken) {
        boardService.deleteBoard(id, bearerToken);
        return ResponseUtil.successMessage("board/delete process is complete");
    }

}
