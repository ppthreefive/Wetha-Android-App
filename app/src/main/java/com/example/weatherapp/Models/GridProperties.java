package com.example.weatherapp.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class GridProperties {
    private String gridX;
    private String gridY;
    private String gridId;
}
