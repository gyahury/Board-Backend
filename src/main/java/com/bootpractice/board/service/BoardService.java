package com.bootpractice.board.service;

import com.bootpractice.board.domain.Board;
import com.bootpractice.board.domain.Member;
import com.bootpractice.board.dto.BoardCreateDto;
import com.bootpractice.board.exception.BoardNotFoundException;
import com.bootpractice.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Board createBoard(BoardCreateDto boardCreateDto, Member member) {

        Board board = new Board();
        board.setTitle(boardCreateDto.getTitle());
        board.setContent(boardCreateDto.getContent());
        board.setMember(member);

        return boardRepository.save(board);
    }

    public Page<Board> getBoards(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    public Optional<Board> getBoardById(Long id) {
        return boardRepository.findById(id);
    }

    public Board updateBoard(Long id, Board updatedBoard) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new BoardNotFoundException());
        board.setTitle(updatedBoard.getTitle());
        board.setContent(updatedBoard.getContent());
        return boardRepository.save(board);
    }

    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }
}