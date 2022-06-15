package ru.javarush.project2.zoo.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class LandTile {

    /**
     * It's a tradeoff between complexity and speed. Hashmap added to prevent whole tile blocking by just 1 action
     * of some creature. Not sure, it's the best decision, probably need to loadtest with profiler someday.
     */
    private Map<String, List<TileItem>> tileItems = new HashMap<>();

}
