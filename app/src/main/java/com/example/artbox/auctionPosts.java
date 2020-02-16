package com.example.artbox;

public class auctionPosts {
    private String post;
    private String details;
    private String price;
    private String hour;

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
