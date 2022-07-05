package com.sparta.finalprojectback.postComment.model;

import com.sparta.finalprojectback.member.Member;
import com.sparta.finalprojectback.member.Timestamped;
import com.sparta.finalprojectback.post.model.Post;
import com.sparta.finalprojectback.post.repository.PostRepository;
import com.sparta.finalprojectback.postComment.dto.PostCommentRequestDto;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class PostComment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public PostComment(String comment, Post post, Member member) {
        this.comment = comment;
        this.post = post;
        this.member = member;
    }
}
