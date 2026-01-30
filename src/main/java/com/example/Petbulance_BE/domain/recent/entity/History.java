package com.example.Petbulance_BE.domain.recent.entity;

import com.example.Petbulance_BE.domain.recent.type.SearchType;
import com.example.Petbulance_BE.domain.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "history", uniqueConstraints = {
        @UniqueConstraint(
                name = "uq_user_hospital_type",
                columnNames = {"user_id", "hospital_id", "search_type"}
        )
})
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SearchType searchType;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private String content;

    private Long hospitalId;

    private String hospitalName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;
}
