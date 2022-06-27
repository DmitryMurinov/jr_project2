package ru.javarush.project2.zoo.service;

import ru.javarush.project2.zoo.factory.TileItemFactory;
import ru.javarush.project2.zoo.model.Land;
import ru.javarush.project2.zoo.model.TileItem;
import ru.javarush.project2.zoo.util.ZooProperties;

public class Gardener implements AnimateLand {

    private TileItemFactory factory = new TileItemFactory();

    private int attemptsToFindTileWithFreeSpace = 3;

    @Override
    public void perform(Land land, ZooProperties zooProperties){
        int plantsPerTick = Integer.parseInt(zooProperties.getProperty("vegetation.per.tick"));

        for (int i = 0; i < plantsPerTick; i++) {
            plant(land, zooProperties);
        }

    }

    private void plant(Land land, ZooProperties zooProperties) {
        TileItem plant = factory.make(ZooProperties.VEGETATION_TYPE_NAME, zooProperties);

        int landSizeY = Integer.parseInt(zooProperties.getProperty("land.size.y"));
        int landSizeX = Integer.parseInt(zooProperties.getProperty("land.size.x"));

        for (int i = 0; i < attemptsToFindTileWithFreeSpace; i++) {
            int xPosition = java.util.concurrent.ThreadLocalRandom.current().nextInt(0, landSizeX);
            int yPosition =  java.util.concurrent.ThreadLocalRandom.current().nextInt(0, landSizeY);

            int actualVegetationOnTile = land.getIsland()[yPosition][xPosition].getTileItems().get(ZooProperties.VEGETATION_TYPE_NAME).size();
            int maxVegetationOntile = zooProperties.getItemsProps().get(ZooProperties.VEGETATION_TYPE_NAME).getMaxOnTileCount();

            if(actualVegetationOnTile >= maxVegetationOntile){
                continue;
            }

            plant.setPosY(yPosition);
            plant.setPosX(xPosition);

            land.getIsland()[yPosition][xPosition].getTileItems().get(ZooProperties.VEGETATION_TYPE_NAME).add(plant);
            return;
        }
    }


}
