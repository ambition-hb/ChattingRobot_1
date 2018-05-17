package com.example.chattingrobot_1;

/**
 * 定义消息的实体类
 * Created by 浩比 on 2018/5/15.
 */

public class Msg {

    public static final int TYPE_RECEIVED = 0;

    public static final int TYPE_SENT = 1;

    private String content;//消息内容

    private int type;//消息类型

    public Msg(String content, int type){
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }
}
