package ru.javarush.project2.zoo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.javarush.project2.zoo.factory.TileItemFactory;
import ru.javarush.project2.zoo.util.ZooProperties;

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

    /**
     * Is it possible to do something with this item by it's own or other methods
     * If isAlive = false, only removing is possible
     */
    private boolean isAlive;

    private TileItemFactory tileItemFactory = new TileItemFactory();

    public TileItem(ItemProperties value) {
        value.getCounter().incrementAndGet();
        this.typeName = value.getTypeName();
        this.name = value.getTypeName() + "-" + value.getId();
        this.weight = value.getWeight();
        this.maxOnTileCount = value.getMaxOnTileCount();
        this.isAlive = true;
    }

    public void die(){
        isAlive = false;
    }

    public void liveTick(Land land, ZooProperties zooProperties){};

}
