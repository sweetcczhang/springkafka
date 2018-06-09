package cn.bupt.zcc.beans;

import java.util.Date;

/**
 * Created by 张城城 on 2018/6/8.
 */
public class Message {

    private Long id;    //id

    private String msg;     //消息

    private Date sendTime;      //发送消息的时间

    public Message(Long id, String msg, Date sendTime) {
        this.id = id;
        this.msg = msg;
        this.sendTime = sendTime;
    }
    public Message(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }
}
