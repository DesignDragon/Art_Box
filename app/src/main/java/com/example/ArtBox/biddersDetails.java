package com.example.ArtBox;

class biddersDetails {
    public String getBid_amount() {
        return bid_amount;
    }

    public void setBid_amount(String bid_amount) {
        this.bid_amount =  bid_amount;
    }

    public String getBidder_name() {
        return bidder_name;
    }

    public void setBidder_name(String bidder_name) {
        this.bidder_name = bidder_name;
    }

    private String bid_amount;
    private String bidder_name;

    public String getBidder_id() {
        return bidder_id;
    }

    public void setBidder_id(String bidder_id) {
        this.bidder_id = bidder_id;
    }

    private String bidder_id;
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    private String time;
}
