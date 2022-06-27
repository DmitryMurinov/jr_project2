package ru.javarush.project2.zoo.service;

import ru.javarush.project2.zoo.model.Land;
import ru.javarush.project2.zoo.model.TileItem;
import ru.javarush.project2.zoo.util.ZooProperties;

import java.util.List;
import java.util.Set;

public class Hearse implements AnimateLand {

    @Override
    public void perform(Land land, ZooProperties zooProperties) {

        int landSizeX = Integer.parseInt(zooProperties.getProperty("land.size.x"));
        int landSizeY = Integer.parseInt(zooProperties.getProperty("land.size.y"));

        for (int y = 0; y < landSizeY; y++) {
            for (int x = 0; x < landSizeX; x++) {
         
                Set<String> types = zooProperties.getItemsProps().keySet();
                for(String type : types) {
                    synchronized (land.getIsland()[y][x].getTileItems().get(type)){
                        List<TileItem> items = land.getIsland()[y][x].getTileItems().get(type);
                        items.removeIf(i -> !i.isAlive());

                    }
                }
            }
        }
    }

}