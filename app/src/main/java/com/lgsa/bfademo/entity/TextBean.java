package com.lgsa.bfademo.entity;


public class TextBean {
    public String id;
    public String time;
    public String content;
    public String location;

    public String detail;
    public byte[] photo;

    public TextBean(String id,String time, String content, String location) {
        this.id = id;
        this.time = time;
        this.content=content;
        this.location=location;
    }



    public String getTime(){
        return time;
    }
    public void setTime(){
        this.time=time;
    }

    public String getContent(){
        return content;
    }
    public void setContent(){
        this.content=content;
    }

    public String getLocation(){
        return location;
    }
    public void setLocation(){
        this.location=location;
    }

    public String getId() {
        return id;
    }
    public void setId(){
        this.id=id;
    }

    public String getDetail() {
        return detail;
    }
    public void setDetail(){
        this.detail=detail;
    }

    public byte[] getPhoto() {
        return photo;
    }
    public void setPhoto(){
        this.photo=photo;
    }
    public TextBean(){}
}
