package com.sparta.neonaduriback.openvidu.dto;

import com.sparta.neonaduriback.openvidu.AudioChatRole;
import lombok.Data;

@Data
public class AudioChatEntryDto {
    private Long postId;
    private String nickName;
    private AudioChatRole role;
    private Long participantCount;
}
