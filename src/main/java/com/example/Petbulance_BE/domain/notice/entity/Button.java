package com.example.Petbulance_BE.domain.notice.entity;

import com.example.Petbulance_BE.domain.notice.dto.request.UpdateNoticeReqDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "buttons")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Button {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "button_id")
    private Long id;

    // Button.java 내 필드 수정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id") // DB 컬럼명 확인 (기존 소스에 user_id라고 되어있었으니 확인 필요)
    private Notice notice;

    private String text;
    private String position;
    private String link;
    private String target;

    public void setNotice(Notice notice) {
        this.notice = notice;
    }

    // 버튼 내용 수정을 위한 메서드
    public void update(UpdateNoticeReqDto.ButtonReqDto dto) {
        this.text = dto.getText();
        this.position = dto.getPosition();
        this.link = dto.getLink();
        this.target = dto.getTarget();
    }

}
