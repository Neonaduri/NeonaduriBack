package com.sparta.neonaduriback.login.service;

/**
 * [Service] - 회원가입 Service
 *
 * @class : UserService
 * @author : 오예령
 * @since   : 2022.04.30
 * @version : 1.0
 * <p>
 * 수정일     수정자             수정내용
 * --------   --------    ---------------------------
 */


import com.amazonaws.services.s3.AmazonS3Client;
import com.sparta.neonaduriback.common.image.model.Image;
import com.sparta.neonaduriback.common.image.repository.ImageRepository;
import com.sparta.neonaduriback.common.image.service.S3Uploader;
import com.sparta.neonaduriback.like.repository.LikeRepository;
import com.sparta.neonaduriback.login.dto.PasswordRequestDto;
import com.sparta.neonaduriback.login.dto.SignupRequestDto;
import com.sparta.neonaduriback.login.model.User;
import com.sparta.neonaduriback.login.repository.UserRepository;
import com.sparta.neonaduriback.login.validator.UserInfoValidator;
import com.sparta.neonaduriback.post.model.Post;
import com.sparta.neonaduriback.post.repository.PostRepository;
import com.sparta.neonaduriback.review.model.Review;
import com.sparta.neonaduriback.review.repository.ReviewRepository;
import com.sparta.neonaduriback.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passWordEncoder;
    private final UserInfoValidator userInfoValidator;
    private final ImageRepository imageRepository;
    private final S3Uploader s3uploader;
    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;
    private final LikeRepository likeRepository;
    private final AmazonS3Client amazonS3Client;

    //회원가입
    @Transactional
    public ResponseEntity<String> registerUser(SignupRequestDto signupRequestDto, Errors errors) {
        String message = userInfoValidator.getValidMessage(signupRequestDto, errors);
        if (message.equals("회원가입 성공")) {
            String userName = signupRequestDto.getUserName();
            //비밀번호 암호화
            String passWordEncode = passWordEncoder.encode(signupRequestDto.getPassword());
            //저장할 유저 객체 생성
            User user = new User(userName, passWordEncode, signupRequestDto);
            //회원정보 저장
            userRepository.save(user);
            return ResponseEntity.status(201).body("회원가입 성공");
        } else {
            return ResponseEntity.status(400).body(message);
        }
    }

    //유저 프로필 수정
    @Transactional
    public void updateUserInfo(MultipartFile multipartFile, String nickName, Long userId) throws IOException {

        System.out.println("UserService에서 받는 userId = " + userId);
        String profileImgUrl = s3uploader.updateImage(multipartFile, "static", userId);
        System.out.println("UserService에서 받는 profileImgUrl = " + profileImgUrl);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("회원 정보가 없습니다")
        );
        user.update(profileImgUrl, nickName);
        userRepository.save(user);
    }

    //사진 삭제했거나 이미지 수정 안했을 시
    @Transactional
    public void deleteProfileImg(String profileImgUrl, String nickName, Long userId) {

        //사진 삭제시 -> 기본이미지로 변경
        if (profileImgUrl.equals("")) {
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new IllegalArgumentException("해당 유저가 없습니다")
            );

            Image image =imageRepository.findByImageUrlAndUserId(user.getProfileImgUrl(), userId).orElse(null);
            
            if(image==null){
                log.info("기본 사진이거나 유저의 기존 프로필 사진을 찾을 수 없습니다.");
                user.update(nickName);
                userRepository.save(user);
            }else{
                imageRepository.deleteByUserId(userId);
                s3uploader.deleteImage(image.getFilename());
                //디폴트 이미지
                profileImgUrl = "https://seunghodev-bucket.s3.ap-northeast-2.amazonaws.com/default/Group+1.png";
                user.update(profileImgUrl, nickName);
                userRepository.save(user);
                log.warn("기본이미지로 변경되었습니다.");
            }
        }
        //수정 안했을 시(url은 존재하는 경우)
        else {
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new IllegalArgumentException("해당 유저가 없습니다")
            );
            user.update(profileImgUrl, nickName);
            userRepository.save(user);
        }
    }

    // 비밀번호 변경
    @Transactional
    public ResponseEntity<Boolean> updatePassword(PasswordRequestDto requestDto, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        if (!passWordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호를 다시 확인해 주세요.");
        }
        String password = passWordEncoder.encode(requestDto.getNewPassword());

        user.updateUserPassword(password);
        userRepository.save(user);

        return ResponseEntity.status(201).body(true);
    }

    // 회원 탈퇴
    @Transactional
    public ResponseEntity<String> withdrawal(UserDetailsImpl userDetails) throws Exception {

        Long userId = userDetails.getUser().getId();

        // 탈퇴한 유저의 후기에 사진이 남아있는 경우 삭제
        List<Review> reviewList = reviewRepository.findAllByUser(userDetails.getUser());

        for (Review review : reviewList) {
            String reviewImgUrl = review.getReviewImgUrl();
            Optional<Image> image = imageRepository.findByImageUrl(reviewImgUrl);

            if(image.isPresent()) {
                String fileName = image.get().getFilename();

                s3uploader.deleteImage(fileName);
                imageRepository.deleteByFilename(fileName);
            }
        }
        // 탈퇴한 유저의 프로필이미지 삭제
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new IllegalArgumentException("해당 유저가 없습니다")
        );

        Optional<Image> image = imageRepository.findByUserId(userId);

        if(image.isPresent()){
            String fileName = image.get().getFilename();

            s3uploader.deleteImage(fileName);
            imageRepository.deleteByUserId(userId);
        }

        // 탈퇴한 유저의 게시글 비활성화
        List<Post> postList = postRepository.findAllByUser(user);
        for (Post post : postList) {
            if (post != null) {
                post.setUser(null);
                postRepository.save(post);
            }
        }

        // 탈퇴한 유저의 후기 삭제
        reviewRepository.deleteAllByUser(user);

        // 찜 삭제
//        List<Likes> likesList = likeRepository.findAllByUserId(userId);
//        for (Likes likes : likesList) {
//            if (likes != null) {
//                likes.setUserId(null);
//                likes.setPostId(null);
//                likeRepository.save(likes);
//            }
//        }
        userRepository.deleteById(userId);
        return ResponseEntity.status(201).body("201");
    }

}
