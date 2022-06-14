package ru.javarush.project2.zoo.utils;

import ru.javarush.project2.zoo.model.*;

import java.util.List;
import java.util.Map;

public class Populate {

    private Land land = Land.getInstance();

    private int landSizeX = Integer.parseInt(ZooProperties.getProperty("land.size.x"));
    private int landSizeY = Integer.parseInt(ZooProperties.getProperty("land.size.y"));


    public void fillLandWithLife() {

        createLand();

        for(Map.Entry<String, TileItem> entry : ZooProperties.getItemsProps().entrySet()){
            if(ZooProperties.VEGETATION_TYPE_NAME.equals(entry.getKey())){
                plantVegetation(entry);
                continue;
            }

            String typeName = entry.getValue().getTypeName();

            //If creature can eat vegetation, it's a herbivorous creature
            if(ZooProperties.getEatChain().get(typeName).containsKey(ZooProperties.VEGETATION_TYPE_NAME)){
                createHerbivorous(entry);
                continue;
            }


            //If creature can't eat vegetation, it's a carnivorous creature
            if(!ZooProperties.getEatChain().get(typeName).containsKey(ZooProperties.VEGETATION_TYPE_NAME)){
                createCarnivorous(entry);
            }
        }
    }

    private void createLand() {
        land.setIsland(new LandTile[landSizeY][landSizeX]);

        for (int y = 0; y < landSizeY; y++) {
            for (int x = 0; x < landSizeX; x++) {
                land.getIsland()[y][x] = new LandTile();
            }
        }
    }

    /**
     * Current logic tries to create carnivorous creatures on tiles without their prey.
     */
    private void createCarnivorous(Map.Entry<String, TileItem> entry) {
        for (int i = 0; i < entry.getValue().getMaxTileCount(); i++) {
            Carnivorous carnivorous = new Carnivorous(entry.getValue());

            int xPosition = java.util.concurrent.ThreadLocalRandom.current().nextInt(0, landSizeX);
            int yPosition =  java.util.concurrent.ThreadLocalRandom.current().nextInt(0, landSizeY);

            boolean preyOnTile = checkPrey(entry, xPosition, yPosition);

            int attemptsToFindTileWithoutPrey = 10;
            while (preyOnTile && attemptsToFindTileWithoutPrey > 0){
                xPosition = java.util.concurrent.ThreadLocalRandom.current().nextInt(0, landSizeX);
                yPosition =  java.util.concurrent.ThreadLocalRandom.current().nextInt(0, landSizeY);

                preyOnTile = checkPrey(entry, xPosition, yPosition);
                attemptsToFindTileWithoutPrey--;
            }

            carnivorous.setPosX(xPosition);
            carnivorous.setPosY(yPosition);

            land.getIsland()[yPosition][xPosition].getTileItems().add(carnivorous);
        }
    }

    private boolean checkPrey(Map.Entry<String, TileItem> entry, int xPosition, int yPosition) {
        List<TileItem> tileItems = land.getIsland()[yPosition][xPosition].getTileItems();

        return tileItems.stream().anyMatch(tileItem ->
             ZooProperties.getEatenChain().get(tileItem.getTypeName()) != null &&
                     ZooProperties.getEatenChain().get(tileItem.getTypeName()).containsKey(entry.getKey()));
    }

    private void createHerbivorous(Map.Entry<String, TileItem> entry) {
        for (int i = 0; i < entry.getValue().getMaxTileCount(); i++) {
            Herbivorous herbivorous = new Herbivorous(entry.getValue());

            int xPosition = java.util.concurrent.ThreadLocalRandom.current().nextInt(0, landSizeX);
            int yPosition =  java.util.concurrent.ThreadLocalRandom.current().nextInt(0, landSizeY);

            herbivorous.setPosX(xPosition);
            herbivorous.setPosY(yPosition);

            land.getIsland()[yPosition][xPosition].getTileItems().add(herbivorous);
        }
    }

    private void plantVegetation(Map.Entry<String, TileItem> entry) {
        for (int i = 0; i < entry.getValue().getMaxTileCount(); i++) {
            Plant plant = new Plant(entry.getValue());

            int xPosition = java.util.concurrent.ThreadLocalRandom.current().nextInt(0, landSizeX);
            int yPosition =  java.util.concurrent.ThreadLocalRandom.current().nextInt(0, landSizeY);

            land.getIsland()[yPosition][xPosition].getTileItems().add(plant);
        }
    }
}
