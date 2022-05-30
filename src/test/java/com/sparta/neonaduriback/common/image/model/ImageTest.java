package com.sparta.neonaduriback.common.image.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageTest {

    @Test
    @DisplayName("이미지 repo 테스트")
    public void imageTest(){

        //given
        String filename="파일네임";
        String imageUrl="이미지유알엘";
        Long userId=1L;

        //when
        Image image = new Image(filename, imageUrl, userId);

        //then
        assertNull(image.getId());
        assertEquals(filename, image.getFilename());
        assertEquals(imageUrl, image.getImageUrl());
        assertEquals(userId, image.getUserId());
    }


}