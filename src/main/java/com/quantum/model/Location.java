package com.quantum.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "location")
public class Location {

    @Id
    private UUID restaurantId;

    private double latitude;

    private double longitude;

    @OneToOne
    @MapsId
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(UUID restaurantId) {
        this.restaurantId = restaurantId;
    }
}
