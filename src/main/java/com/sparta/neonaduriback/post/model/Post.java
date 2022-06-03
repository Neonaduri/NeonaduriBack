package com.sparta.neonaduriback.post.model;

import com.sparta.neonaduriback.common.model.Timestamped;
import com.sparta.neonaduriback.login.model.User;
import com.sparta.neonaduriback.post.days.model.Days;
import com.sparta.neonaduriback.post.dto.PostRequestDto;
import com.sparta.neonaduriback.post.dto.RoomMakeRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Post extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false)
    private String postUUID;

    @Column(nullable = false)
    private String startDate;

    @Column(nullable = false)
    private String endDate;

    @Column(nullable = false)
    private int dateCnt;

    @Column(nullable = false)
    private String postTitle;

    @Column(nullable = false)
    private String location;

    @Column(nullable = true, length = 500)
    private String postImgUrl;

    @Column(nullable = false)
    private String theme;

    @Column(nullable = true)
    private boolean islike;

    @Column(nullable = true)
    private int likeCnt;

    @Column(nullable = true)
    private boolean ispublic;

    @Column(nullable = true)
    private int viewCnt;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "POST_ID")
    private List<Days> days = new ArrayList<>();

    public static String makeShortUUID() {
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        String str = Long.toString(l, Character.MAX_RADIX);
        return str;
    }

    //방 만들어줄 때 생성자
    public Post(RoomMakeRequestDto roomMakeRequestDto, User user){
        this.postUUID=makeShortUUID();
        this.startDate=roomMakeRequestDto.getStartDate();
        this.endDate=roomMakeRequestDto.getEndDate();
        this.dateCnt=roomMakeRequestDto.getDateCnt();
        this.postTitle=roomMakeRequestDto.getPostTitle();
        this.location=roomMakeRequestDto.getLocation();
        this.theme=roomMakeRequestDto.getTheme();
        this.user=user;
    }
    //저장할때 추가로 필요한 post정보
    public void completeSave(PostRequestDto postRequestDto, List<Days> daysList){
        this.postImgUrl=postRequestDto.getPostImgUrl();
        this.ispublic=postRequestDto.isIspublic();
        this.days=daysList;
    }

    //likeCnt 정보 수정
    public void updateLikeCnt(int likeCnt){
        this.likeCnt=likeCnt;
    }

}

