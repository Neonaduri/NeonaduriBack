package com.sparta.neonaduriback.review.model;

/**
 * [model] - Review
 *
 * @class   : Review
 * @author  : 오예령
 * @since   : 2022.05.03
 * @version : 1.0
 *
 *   수정일     수정자             수정내용
 *  --------   --------    ---------------------------
 *
 */

import com.sparta.neonaduriback.common.model.Timestamped;
import com.sparta.neonaduriback.login.model.User;
import com.sparta.neonaduriback.review.dto.ReviewWithdrawalDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Review extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reviewContents;

    @Column(nullable = true, length = 500)
    private String reviewImgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_Id")
    private User user;

    @Column(nullable = false)
    private Long postId;

    // 후기 등록(사진도 같이등록했을 시)
    public Review(String reviewContents, String reviewImgUrl, User user, Long postId) {
        this.reviewContents = reviewContents;
        this.reviewImgUrl = reviewImgUrl;
        this.user = user;
        this.postId = postId;
    }
    // 후기 등록(내용만 등록 시)
    public Review(String reviewContents, User user, Long postId){
        this.reviewContents=reviewContents;
        this.user=user;
        this.postId=postId;
    }

    //후기 수정
    public void update(String reviewContents,String reviewImgUrl){
        this.reviewContents=reviewContents;
        this.reviewImgUrl=reviewImgUrl;
    }

}
