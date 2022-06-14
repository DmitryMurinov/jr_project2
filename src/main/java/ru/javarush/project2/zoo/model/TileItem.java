package ru.javarush.project2.zoo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicLong;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TileItem {

    /**
     * Used for naming
     */
    private AtomicLong id = new AtomicLong(0);

    /**
     * Used for population control
     */
    private AtomicLong counter = new AtomicLong(0);

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
    private int maxTileCount;

    /**
     * For how many tiles creature can move in 1 tick
     */
    private int maxTickMove;

    /**
     * Weight, used to calculate how much food will get eater of this thing
     */
    private double weight;

    /**
     * Max food for this type (=max HP in games).
     */
    private double maxFood;

    /**
     * Current food for this type (=current HP in games). When currentFood reaches 0 creature will starve to death.
     */
    private double currentFood;

    public TileItem(TileItem value) {
        value.getCounter().incrementAndGet();
        this.typeName = value.getTypeName();
        this.name = value.getTypeName() + "-" + value.getId().incrementAndGet();
        this.weight = value.getWeight();
        this.maxTickMove = value.getMaxTickMove();
        this.maxFood = value.getMaxFood();
    }

}
