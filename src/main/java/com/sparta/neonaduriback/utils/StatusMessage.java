package com.sparta.neonaduriback.utils;

import lombok.Data;

@Data
public class StatusMessage {

    private StatusEnum status;
    private Object data;

    public StatusMessage(){
        this.status = StatusEnum.BAD_REQUEST;
    }

}