package com.example.Petbulance_BE.domain.comment.entity;

import com.example.Petbulance_BE.domain.comment.dto.request.UpdatePostCommentReqDto;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.mapped.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PostComment parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mention_user_id")
    private Users mentionUser;

    @Column(name = "is_secret", nullable = false)
    private Boolean isSecret;

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean hidden = false;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    public void update(UpdatePostCommentReqDto dto) {
        this.content = dto.getContent();
        this.imageUrl = dto.getImageUrl();
        this.isSecret = dto.isSecret();
    }

    // 상위댓글인지 확인
    public boolean isRoot() {
        return parent == null;
    }

    // 삭제 표시
    public void delete() {
        deleted = true;
    }

}
