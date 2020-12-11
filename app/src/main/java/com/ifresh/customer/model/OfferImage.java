package com.ifresh.customer.model;

public class OfferImage {
    private String parent_id,  image, youtube_str, offer_title;
    private int is_imgscroll;

    public String getOffer_title() {
        return offer_title;
    }

    public void setOffer_title(String offer_title) {
        this.offer_title = offer_title;
    }

    public int getIs_imgscroll() {
        return is_imgscroll;
    }

    public void setIs_imgscroll(int is_imgscroll) {
        this.is_imgscroll = is_imgscroll;
    }

    public String getYoutube_str() {
        return youtube_str;
    }

    public void setYoutube_str(String youtube_str) {
        this.youtube_str = youtube_str;
    }

    public String getId() {
        return parent_id;
    }

    public void setId(String id) {
        this.parent_id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
