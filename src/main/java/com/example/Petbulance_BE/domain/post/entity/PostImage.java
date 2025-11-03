package com.example.Petbulance_BE.domain.post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_images")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;

    @Column(name = "image_order", nullable = false)
    private int imageOrder;

    @Column(name = "is_thumbnail", nullable = false)
    private boolean thumbnail;

    public static PostImage create(Post post, String imageUrl, int order, boolean isThumbnail) {
        return PostImage.builder()
                .post(post)
                .imageUrl(imageUrl)
                .imageOrder(order)
                .thumbnail(isThumbnail)
                .build();
    }

    public void updateOrderAndThumbnail(int order, boolean isThumbnail) {
        this.imageOrder = order;
        this.thumbnail = isThumbnail;
    }
}

