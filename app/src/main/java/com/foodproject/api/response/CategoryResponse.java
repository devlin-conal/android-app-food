package com.foodproject.api.response;

import java.io.Serializable;
import java.util.Collection;

public class CategoryResponse implements Serializable {


    private Long id;
    private String name;
    private String thumbnailPic;
    private Collection<RestaurantResponse> restaurants;

    public CategoryResponse() {
    }

    public CategoryResponse(Long id, String name, String thumbnailPic, Collection<RestaurantResponse> restaurants) {
        this.id = id;
        this.name = name;
        this.thumbnailPic = thumbnailPic;
        this.restaurants = restaurants;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailPic() {
        return thumbnailPic;
    }

    public void setThumbnailPic(String thumbnailPic) {
        this.thumbnailPic = thumbnailPic;
    }

    public Collection<RestaurantResponse> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(Collection<RestaurantResponse> restaurants) {
        this.restaurants = restaurants;
    }
}
