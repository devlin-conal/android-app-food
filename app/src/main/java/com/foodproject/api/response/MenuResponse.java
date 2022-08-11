package com.foodproject.api.response;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

public class MenuResponse implements Serializable {
    private Long id;
    private Double price;
    private String thumbnailPic;
    private String name;
    private String description;
    private Double rating;
    private Collection<IngredientResponse> ingredients;

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public Collection<IngredientResponse> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Collection<IngredientResponse> ingredients) {
        this.ingredients = ingredients;
    }

    public MenuResponse(Long id, Double price, String thumbnailPic, String name, String description, Double rating, Collection<IngredientResponse> ingredients) {
        this.id = id;
        this.price = price;
        this.thumbnailPic = thumbnailPic;
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.rating = rating;
    }

    public MenuResponse() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":")
                .append(id);
        sb.append(",\"price\":")
                .append(price);
        sb.append(",\"thumbnailPic\":\"")
                .append(Objects.toString(thumbnailPic, "")).append('\"');
        sb.append(",\"name\":\"")
                .append(Objects.toString(name, "")).append('\"');
        sb.append(",\"description\":\"")
                .append(Objects.toString(description, "")).append('\"');
        sb.append(",\"rating\":")
                .append(rating);
        sb.append(",\"ingredients\":");
        if ((ingredients) != null && !(ingredients).isEmpty()) {
            sb.append("[");
            for (Object collectionValue : ingredients) {
                sb.append("\"").append(Objects.toString(collectionValue, "")).append("\",");
            }
            sb.replace(sb.length() - 1, sb.length(), "]");
        } else {
            sb.append("[]");
        }
        sb.append('}');
        return sb.toString();
    }
}
