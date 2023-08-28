package com.bootpractice.board.service;

import com.bootpractice.board.domain.Board;
import com.bootpractice.board.domain.Member;
import com.bootpractice.board.dto.BoardCreateDto;
import com.bootpractice.board.dto.BoardUpdateDto;
import com.bootpractice.board.exception.BoardNotFoundException;
import com.bootpractice.board.exception.MemberNotFoundException;
import com.bootpractice.board.exception.UserMismatchException;
import com.bootpractice.board.repository.BoardRepository;
import com.bootpractice.board.repository.MemberRepository;
import com.bootpractice.board.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class BoardService {

    @Value("${jwt.secret}")
    private String secretkey;

    private final BoardRepository boardRepository;
    private final MemberService memberService;

    public BoardService(BoardRepository boardRepository, MemberService memberService) {
        this.boardRepository = boardRepository;
        this.memberService = memberService;
    }

    public void createBoard(BoardCreateDto boardCreateDto, String bearerToken) {

        String token = bearerToken.split(" ")[1];
        String email = JwtUtil.getEmail(token, secretkey);

        Member member = memberService.findMemberByEmail(email);

        Board board = new Board();
        board.setTitle(boardCreateDto.getTitle());
        board.setContent(boardCreateDto.getContent());
        board.setMember(member);

        boardRepository.save(board);
    }

    public Page<Board> getAllBoards(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    public Board getBoardById(Long id) {
        return boardRepository.findById(id).orElseThrow(() -> new BoardNotFoundException());
    }

    public void updateBoard(Long id, BoardUpdateDto boardUpdateDto, String bearerToken) {

        String token = bearerToken.split(" ")[1];
        String tokenEmail = JwtUtil.getEmail(token, secretkey);

        Board board = boardRepository.findById(id).orElseThrow(() -> new BoardNotFoundException());

        if(tokenEmail.equals(board.getMember().getEmail())){
            board.setTitle(boardUpdateDto.getTitle());
            board.setContent(boardUpdateDto.getContent());
            boardRepository.save(board);
        }else{
            throw new UserMismatchException();
        }

    }

    public void deleteBoard(Long id, String bearerToken) {

        String token = bearerToken.split(" ")[1];
        String tokenEmail = JwtUtil.getEmail(token, secretkey);

        Board board = boardRepository.findById(id).orElseThrow(() -> new BoardNotFoundException());

        if(tokenEmail.equals(board.getMember().getEmail())){
            boardRepository.deleteById(id);
        }else{
            throw new UserMismatchException();
        }
    }
}