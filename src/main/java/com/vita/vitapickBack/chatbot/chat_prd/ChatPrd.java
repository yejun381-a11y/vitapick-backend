package com.vita.vitapickBack.chatbot.chat_prd;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// chat_prd 테이블 엔티티 - 챗봇 추천상품 저장
@Entity
@Table(name="chat_prd")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatPrd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_prd_id")
    private Long chatPrdId;

    @Column(name = "msg_id", nullable = false)
    private Long msgId;

    @Column(name = "prd_id", nullable = true)
    private Long prdId;

    @Column(name = "sort_num", nullable = false)
    private Integer sortNum;

    @Column(name = "chat_rec_reason", columnDefinition = "TEXT")
    private String chatRecReason;
}