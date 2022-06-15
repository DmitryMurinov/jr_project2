package ru.javarush.project2.zoo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class TileItem {

    /**
     * X Position on field, -1 in case when object not on field (used for type reference)
     */
    private int posX;

    /**
     * Y Position on field, -1 in case when object not on field (used for type reference)
     */
    private int posY;

    /**
     * Name of kind, like "Wolf"
     */
    private String typeName;

    /**
     * Unique creature name, like "Wolf-1"
     */
    private String name;

    /**
     * How many of such creatures can be on 1 tile of field in same time
     */
    private int maxOnTileCount;

    /**
     * Weight, used to calculate how much food will get eater of this thing
     */
    private double weight;

    public TileItem(ItemProperties value) {
        value.getCounter().incrementAndGet();
        this.typeName = value.getTypeName();
        this.name = value.getTypeName() + "-" + value.getId();
        this.weight = value.getWeight();
        this.maxOnTileCount = value.getMaxOnTileCount();
    }

    public void die(Land land){
        land.getIsland()[posY][posX].getTileItems().remove(this);
    }

}
