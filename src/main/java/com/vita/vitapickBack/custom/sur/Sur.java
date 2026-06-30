package com.vita.vitapickBack.custom.sur;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sur")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sur {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sur_id")
	private Long surId;
	
	@Column(name = "sur_title", length = 50)
    private String surTitle;

    @Column(name = "user_num")
    private Long userNum;

    @Column(name = "ans_json", columnDefinition = "JSON")
    private String ansJson;
}
