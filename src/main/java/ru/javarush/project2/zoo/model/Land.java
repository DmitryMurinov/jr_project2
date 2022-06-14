package ru.javarush.project2.zoo.model;

import lombok.Getter;
import lombok.Setter;
import ru.javarush.project2.zoo.utils.ZooProperties;

@Getter
@Setter
public class Land {

    private LandTile[][] island;

    private static Land INSTANCE;

    private static ZooProperties properties;

    private Land(){}

    public static Land getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Land();
        }

        return INSTANCE;
    }
}
