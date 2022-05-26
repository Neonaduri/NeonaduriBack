package com.sparta.neonaduriback.review.dto;

import com.sparta.neonaduriback.login.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReviewWithdrawalDto {
    private User user;
    private Long postId;
}
