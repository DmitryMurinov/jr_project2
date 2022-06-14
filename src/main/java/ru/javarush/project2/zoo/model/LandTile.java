package ru.javarush.project2.zoo.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LandTile {

    private List<TileItem> tileItems = new ArrayList<>();

}
