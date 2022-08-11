package com.foodproject.api.response;

import java.io.Serializable;

public class IngredientResponse implements Serializable {
    private Long id;
    private Double price;
    private String thumbnailPic;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getThumbnailPic() {
        return thumbnailPic;
    }

    public void setThumbnailPic(String thumbnailPic) {
        this.thumbnailPic = thumbnailPic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IngredientResponse(Long id, Double price, String thumbnailPic, String name) {
        this.id = id;
        this.price = price;
        this.thumbnailPic = thumbnailPic;
        this.name = name;
    }

    public IngredientResponse() {
    }
}
