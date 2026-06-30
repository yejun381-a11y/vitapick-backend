package com.vita.vitapickBack.chatbot.chat_room;

import java.time.LocalDateTime;
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

// chat_room 테이블 엔티티 - 채팅방 (유저 1명당 ACTIVE 채팅방 1개)
@Entity
@Table(name="chat_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "user_num", nullable = false)
    private Long userNum;

    @Column(name = "chat_st_cd", nullable = false, length = 50)
    private String chatStCd;

    @Column(name = "crt_at", nullable = false)
    private LocalDateTime crtAt;

    @Column(name = "upd_at")
    private LocalDateTime updAt;
}