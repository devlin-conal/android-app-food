package com.foodproject.api.response;

import java.io.Serializable;
import java.util.Collection;

public class RestaurantResponse implements Serializable {
    private Long id;
    private String name;
    private String description;
    private String location;
    private String city;
    private String street;
    private String house;
    private String email;
    private String phone;
    private String thumbnailPic;
    private Double pricing;
    private Double samePrice;
    private boolean status;
    private Double rating;
    private boolean verified;
    private Collection<MenuResponse> menus;

    public RestaurantResponse() {
    }

    public RestaurantResponse(Long id, String name, String description, String location, String city, String street, String house, String email, String phone, String thumbnailPic, Double pricing, Double samePrice, boolean status, Double rating, boolean verified, CategoryResponse category, Collection<MenuResponse> menus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.city = city;
        this.street = street;
        this.house = house;
        this.email = email;
        this.phone = phone;
        this.thumbnailPic = thumbnailPic;
        this.pricing = pricing;
        this.samePrice = samePrice;
        this.status = status;
        this.rating = rating;
        this.verified = verified;
        this.menus = menus;
    }

    public Collection<MenuResponse> getMenus() {
        return menus;
    }

    public void setMenus(Collection<MenuResponse> menus) {
        this.menus = menus;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getThumbnailPic() {
        return thumbnailPic;
    }

    public void setThumbnailPic(String thumbnailPic) {
        this.thumbnailPic = thumbnailPic;
    }

    public Double getPricing() {
        return pricing;
    }

    public void setPricing(Double pricing) {
        this.pricing = pricing;
    }

    public Double getSamePrice() {
        return samePrice;
    }

    public void setSamePrice(Double samePrice) {
        this.samePrice = samePrice;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
