package ru.javarush.project2.zoo.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.atomic.AtomicLong;

@Data
@Accessors(chain = true)
public class ItemProperties {

    /**
     * Used for naming
     */
    private AtomicLong id = new AtomicLong(0);

    /**
     * Used for population control
     */
    private AtomicLong counter = new AtomicLong(0);

    /**
     * Name of kind, like "Wolf"
     */
    private String typeName;

    /**
     * Weight, used to calculate how much food will get eater of this thing
     */
    private Double weight;

    /**
     * How many of such creatures can be on 1 tile of field in same time
     */
    private Integer maxOnTileCount;

    /**
     * For how many tiles creature can move in 1 tick
     */
    private Integer maxTickMove;

    /**
     * Max food for this type (=max HP in games).
     */
    private Double maxFood;

    public long getId(){
        long out = id.incrementAndGet();
        if(out < 0){
            id = new AtomicLong(0);
            out = id.incrementAndGet();
        }

        return out;
    }

}
