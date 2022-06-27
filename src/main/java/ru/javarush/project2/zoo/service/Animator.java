package ru.javarush.project2.zoo.service;

import ru.javarush.project2.zoo.model.Animal;
import ru.javarush.project2.zoo.model.Land;
import ru.javarush.project2.zoo.model.LandTile;
import ru.javarush.project2.zoo.model.TileItem;
import ru.javarush.project2.zoo.util.ZooProperties;

import java.util.Iterator;

public class Animator implements AnimateLand {

    @Override
    public void perform(Land land, ZooProperties zooProperties){

        int landSizeY = Integer.parseInt(zooProperties.getProperty("land.size.y"));
        int landSizeX = Integer.parseInt(zooProperties.getProperty("land.size.x"));

        for (int yPos = 0; yPos < landSizeY; yPos++) {
            for (int xPos = 0; xPos < landSizeX; xPos++) {
                tileLive(land, zooProperties, land.getIsland()[yPos][xPos]);
            }
        }
    }

    private void tileLive(Land land, ZooProperties zooProperties, LandTile landTile) {
        landTile.getTileItems().forEach((type, items) -> {
            if(type.equals(ZooProperties.VEGETATION_TYPE_NAME)){
                return;
            }

            Iterator<TileItem> itemIterator = items.iterator();
            while(itemIterator.hasNext()){
                Animal animal = (Animal) itemIterator.next();
                animal.liveTick(land, zooProperties);
            }
        });
    }

}
