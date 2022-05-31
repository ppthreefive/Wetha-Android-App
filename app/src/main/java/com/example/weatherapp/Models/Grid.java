package com.example.weatherapp.Models;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Grid {
    private GridProperties properties;

    @NonNull
    @Override
    public String toString() {
        return properties.toString();
    }
}
