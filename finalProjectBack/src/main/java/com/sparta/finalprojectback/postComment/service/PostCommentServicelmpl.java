package com.sparta.finalprojectback.postComment.service;

import com.sparta.finalprojectback.member.Member;
import com.sparta.finalprojectback.post.model.Post;
import com.sparta.finalprojectback.post.repository.PostRepository;
import com.sparta.finalprojectback.postComment.dto.PostCommentRequestDto;
import com.sparta.finalprojectback.postComment.dto.PostCommentResponseDto;
import com.sparta.finalprojectback.postComment.model.PostComment;
import com.sparta.finalprojectback.postComment.repository.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommentServicelmpl implements PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;

    @Override
    public ResponseEntity<List<PostComment>> createPostComment(Member member, PostCommentRequestDto requestDto) {
        Post post = postRepository.findById(requestDto.getPostId()).orElseThrow(
                ()-> new IllegalArgumentException("PostId를 찾을수 없음")
        );
        PostComment postComment = new PostComment(requestDto.getComment(), post, member);
        postCommentRepository.save(postComment);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> deletePostComment(Member member, Long commentId) {
        postCommentRepository.deleteById(commentId);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<PostCommentResponseDto>> readMyPostComment(Member member, Long postId) {
        List<PostComment> postComments = postCommentRepository.findAllByPost_Id(postId);
        List<PostCommentResponseDto> postCommentsList = new ArrayList<>();
        for (PostComment postComment : postComments){
            postCommentsList.add(new PostCommentResponseDto(postComment.getCreatedAt(), postComment.getModifiedAt(), postComment.getId(), postComment.getComment(), postComment.getMember(), postComment.getPost()));
        }
        return new ResponseEntity<>(postCommentsList, HttpStatus.OK);
    }
}