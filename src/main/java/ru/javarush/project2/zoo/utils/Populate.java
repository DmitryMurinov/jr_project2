package ru.javarush.project2.zoo.utils;

import ru.javarush.project2.zoo.model.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Populate {

    private Land land;

    private ZooProperties zooProperties;

    private int landSizeX;
    private int landSizeY;

    public Populate(Land land, ZooProperties properties) {
        this.land = land;
        this.zooProperties = properties;

        this.landSizeX = Integer.parseInt(zooProperties.getProperty("land.size.x"));
        this.landSizeY = Integer.parseInt(zooProperties.getProperty("land.size.y"));
    }

    public void fillLandWithLife() {

        createLand();

        for(Map.Entry<String, ItemProperties> entry : zooProperties.getItemsProps().entrySet()){
            if(ZooProperties.VEGETATION_TYPE_NAME.equals(entry.getKey())){
                plantVegetation(entry);
                continue;
            }

            String typeName = entry.getValue().getTypeName();

            //If creature can eat vegetation, it's a herbivorous creature
            if(zooProperties.getEatChain().get(typeName).containsKey(ZooProperties.VEGETATION_TYPE_NAME)){
                createHerbivorous(entry);
                continue;
            }

            //If creature can't eat vegetation, it's a carnivorous creature
            if(!zooProperties.getEatChain().get(typeName).containsKey(ZooProperties.VEGETATION_TYPE_NAME)){
                createCarnivorous(entry);
            }
        }
    }

    private void createLand() {
        land.setIsland(new LandTile[landSizeY][landSizeX]);

        for (int y = 0; y < landSizeY; y++) {
            for (int x = 0; x < landSizeX; x++) {
                LandTile landTile = new LandTile();

                Set<String> names = zooProperties.getItemsProps().keySet();
                for(String name : names){
                    landTile.getTileItems().put(name, new CopyOnWriteArrayList<>());
                }

                land.getIsland()[y][x] = landTile;
            }
        }
    }

    /**
     * Current logic tries to create carnivorous creatures on tiles without their prey.
     */
    private void createCarnivorous(Map.Entry<String, ItemProperties> entry) {
        for (int i = 0; i < entry.getValue().getMaxOnTileCount(); i++) {
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

            land.getIsland()[yPosition][xPosition].getTileItems().get(carnivorous.getTypeName()).add(carnivorous);
        }
    }

    private boolean checkPrey(Map.Entry<String, ItemProperties> entry, int xPosition, int yPosition) {
        Map<String, List<TileItem>> tileItems = land.getIsland()[yPosition][xPosition].getTileItems();

        for(Map.Entry<String, List<TileItem>> tileTypeItems : tileItems.entrySet()){
            if(tileTypeItems.getKey().equals(ZooProperties.VEGETATION_TYPE_NAME)){
                continue;
            }

            if(tileTypeItems.getValue().size() == 0){
                continue;
            }

            //if item on tile can be eaten by carnivorous we are trying to create, there are prey on this tile
            if(zooProperties.getEatenChain().get(tileTypeItems.getKey()) != null &&
                    zooProperties.getEatenChain().get(tileTypeItems.getKey()).containsKey(entry.getKey())){
                return true;
            }
        }

        return false;
    }

    private void createHerbivorous(Map.Entry<String, ItemProperties> entry) {
        for (int i = 0; i < entry.getValue().getMaxOnTileCount(); i++) {
            Herbivorous herbivorous = new Herbivorous(entry.getValue());

            int xPosition = java.util.concurrent.ThreadLocalRandom.current().nextInt(0, landSizeX);
            int yPosition =  java.util.concurrent.ThreadLocalRandom.current().nextInt(0, landSizeY);

            herbivorous.setPosX(xPosition);
            herbivorous.setPosY(yPosition);

            land.getIsland()[yPosition][xPosition].getTileItems().get(entry.getKey()).add(herbivorous);
        }
    }

    private void plantVegetation(Map.Entry<String, ItemProperties> entry) {
        for (int i = 0; i < entry.getValue().getMaxOnTileCount(); i++) {
            Plant plant = new Plant(entry.getValue());

            int xPosition = java.util.concurrent.ThreadLocalRandom.current().nextInt(0, landSizeX);
            int yPosition =  java.util.concurrent.ThreadLocalRandom.current().nextInt(0, landSizeY);

            land.getIsland()[yPosition][xPosition].getTileItems().get(entry.getKey()).add(plant);
        }
    }
}
