package ru.javarush.project2.zoo.model;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.javarush.project2.zoo.util.ZooProperties;

import java.util.*;

/**
 * Represent animal's view of it's surroundings.
 * Basic logic work for current tile and tiles around animal, located within island borders
 */
@Data
@Accessors(chain = true)
public class AnimalView {

    private Map<String, Integer> localCount = new HashMap();
    private int posY;
    private int posX;

    private AnimalView north;
    private AnimalView east;
    private AnimalView south;
    private AnimalView west;

    public List<AnimalView> getAsList(){
        List<AnimalView> out = new ArrayList<>();
        out.add(this);

        if(north != null){
            out.add(north);
        }

        if(east != null){
            out.add(east);
        }

        if(south != null){
            out.add(south);
        }

        if(west != null){
            out.add(west);
        }

        return out;
    }

    public AnimalView(Land land, ZooProperties zooProperties, int posY, int posX) {
        this.posY = posY;
        this.posX = posX;

        Set<String> creatures = zooProperties.getItemsProps().keySet();
        for(String type : creatures){
            localCount.put(type, land.getIsland()[posY][posX].getTileItems().get(type).size());
        }
    }
}
