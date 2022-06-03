package com.sparta.neonaduriback.common.image.service;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.neonaduriback.common.image.model.Image;
import com.sparta.neonaduriback.common.image.repository.ImageRepository;
import com.sparta.neonaduriback.login.model.User;
import com.sparta.neonaduriback.login.repository.UserRepository;
import com.sparta.neonaduriback.review.model.Review;
import com.sparta.neonaduriback.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {
    private final ImageRepository imageRepository;
    private final AmazonS3Client amazonS3Client;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Value("${cloud.aws.s3.bucket}")

    public String bucket;  // S3 버킷 이름

    public String upload(MultipartFile multipartFile, String dirName, Long userId) throws IOException {

        File uploadFile = convert(multipartFile)  // 파일 변환할 수 없으면 에러
                .orElseThrow(() -> new IllegalArgumentException("error: MultipartFile -> File convert fail"));

        String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();   // S3에 저장된 파일 이름

        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드

        removeNewFile(uploadFile);

        Image image = new Image(fileName, uploadImageUrl, userId);

        imageRepository.save(image);

        return uploadImageUrl;
    }

    //------------------------  유저프로필 수정 --------------------------
    //프로필 수정 (이미지 파일 변환)
    public String updateImage(MultipartFile multipartFile, String dirName, Long userId)throws IOException {

        User user = userRepository.findById(userId).orElseThrow(
                ()-> new IllegalArgumentException("해당 유저가 없습니다")
        );
        String imageUrl = user.getProfileImgUrl();

        Optional<Image> image = imageRepository.findByImageUrlAndUserId(imageUrl, userId);

        //디폴트 이미지여서 아직 image레포지토리에 url이 없는 경우 -> 업로드 시킴
        if(!image.isPresent()){
            return upload(multipartFile, dirName, userId);
        }else{
            String fileName = image.get().getFilename();
            System.out.println(fileName);
            //버켓에 없는 파일네임을 지우라하면 에러가 날까? -> 난다..
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
            System.out.println("여기까지 찍히나?!!");
            imageRepository.deleteByUserId(userId);
        }
        return upload(multipartFile, dirName, userId);
    }

    //------------------------- 후기이미지 수정  -----------------------------
    //후기 수정 (이미지 파일 변환)
    public String updateReviewImage(MultipartFile multipartFile, String dirName, Long reviewId, Long userId)throws IOException {

        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("해당 리뷰가 없습니다")
        );
        String imageUrl = review.getReviewImgUrl();

        //기존에 사진 url이 없던 경우가 아니라면
        Optional<Image> image = imageRepository.findByImageUrlAndUserId(imageUrl, userId);

        //디폴트 이미지여서 아직 image레포지토리에 url이 없는 경우 -> 업로드 시킴
        if (!image.isPresent()) {
            return upload(multipartFile, dirName, userId);
        } else {
            String fileName = image.get().getFilename();
            System.out.println(fileName);
            deleteImage(fileName);
            imageRepository.deleteByUserId(userId);
        }
        return upload(multipartFile, dirName, userId);
    }

    // S3로 업로드
    private String putS3(File uploadFile, String fileName) {
        System.out.println("puts3가 문제?1");
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        System.out.println("puts3가 문제?2");
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }
    // 로컬에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {
        System.out.println("로컬지우기가 문제?1");
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }
    // 로컬에 파일 업로드 하기
    private Optional<File> convert(MultipartFile file) throws IOException {
        System.out.println("로컬에업로드가문제?1");
//        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        File convertFile = new File("/home/ubuntu/" + file.getOriginalFilename());
        System.out.println("현재시스템경로>>>:"+System.getProperty("user.dir"));
        System.out.println("convertFile>>>:"+System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        System.out.println("로컬에업로드가문제?2");
        if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
        System.out.println("로컬에업로드가문제?3");
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
        System.out.println("로컬에업로드가문제?4");
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }
    // S3에 올라갔던 사진 삭제
    public void deleteImage(String fileName){
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }
}