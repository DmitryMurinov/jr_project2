package ru.javarush.project2.zoo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LandTile {

    /**
     * It's a tradeoff between complexity and speed. Hashmap added to prevent whole tile blocking by just 1 action
     * of some creature. Not sure, it's the best decision, probably need to loadtest with profiler someday.
     */
    private Map<String, List<TileItem>> tileItems = new HashMap<>();

    private Map<String, Long> tileHistory = new ConcurrentHashMap<>();

}
