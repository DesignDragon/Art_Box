package com.example.ArtBox;

public class auctionPosts {
    private String uid;
    private String post;
    private String details;
    private String price;
    private String hour;
    private String uploadTime;
    private String auctionId;

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }




    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

    public String getPost() { return post; }

    public void setPost(String post) { this.post = post; }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    private String min;

    public auctionPosts() { }

    public String getDetails() { return details; }

    public String getPrice() { return price; }

    public String getUrl() {
        return post;
    }

    public void setUrl(String post) { this.post = post; }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
