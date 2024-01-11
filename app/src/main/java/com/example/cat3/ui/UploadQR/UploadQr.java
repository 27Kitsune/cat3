package com.example.cat3.ui.UploadQR;

public class UploadQr {
    private String imageUrl, Caption;
    public UploadQr(){
        //Keep this constructor with no operation.
    }

    public UploadQr(String imageUrl, String Caption) {

        this.imageUrl = imageUrl;
        this.Caption = Caption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCaption() {return Caption;}

    public void setCaption(String caption) {this.Caption = caption;}
}
