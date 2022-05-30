package com.sparta.neonaduriback.like.service;

import com.sparta.neonaduriback.like.dto.LikeResponseDto;
import com.sparta.neonaduriback.like.model.Likes;
import com.sparta.neonaduriback.like.repository.LikeRepository;
import com.sparta.neonaduriback.login.model.User;
import com.sparta.neonaduriback.post.model.Post;
import com.sparta.neonaduriback.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Transactional
    public LikeResponseDto toggle(Long postId, User user) {
        Long userId=user.getId();
        Optional<Likes> likesOptional=likeRepository.findByPostIdAndUserId(postId, userId);

        LikeResponseDto likeResponseDto = new LikeResponseDto();
        //이미 해당 게시물 찜한 경우
        if(likesOptional.isPresent()){
            //스크랩 내역 삭제
            likeRepository.deleteByPostIdAndUserId(postId, userId);
            //스크랩 false 상태 반환
            likeResponseDto.setLike(false);
        }else{
        //아직 찜 안 한 경우
            //스크랩 시켜줌
            Likes likes=new Likes(userId, postId);
            likeRepository.save(likes);
            //스크랩 true 상태 반환
            likeResponseDto.setLike(true);
        }

        //스크랩 누르거나 취소하는 동시에 게시물 likeCnt 개수도 변화
        //게시물의 스크랩개수 세기 (likeCnt)
        countLikeCnt(postId);

        return likeResponseDto;
    }

    //게시물이 스크랩 몇번 됐는지 반환
    @Transactional
    public void countLikeCnt(Long postId){
        int likeCnt=likeRepository.countByPostId(postId).intValue();
        Post post=postRepository.findById(postId).orElseThrow(
                ()->new IllegalArgumentException("해당 게시물이 없습니다")
        );
        post.updateLikeCnt(likeCnt);
        postRepository.save(post);
    }
}
