package com.example.weatherapp.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Grid {
    private GridProperties properties;

    @Override
    public String toString() {
        return properties.toString();
    }
}
