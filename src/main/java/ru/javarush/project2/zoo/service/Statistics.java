package ru.javarush.project2.zoo.service;

import lombok.Setter;
import ru.javarush.project2.zoo.model.Land;
import ru.javarush.project2.zoo.model.LandTile;
import ru.javarush.project2.zoo.util.ZooProperties;

import java.util.*;

@Setter
public class Statistics implements AnimateLand{

    private long tickNo = 0;

    @Override
    public void perform(Land land, ZooProperties zooProperties) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\tTick No: " + tickNo + "\r\n");

        List<String> typeNames = new ArrayList<>(zooProperties.getItemsProps().keySet());

        stringBuffer.append("\t\t\t");

        for (int i = 0; i < typeNames.size(); i++) {
            String typeName = typeNames.get(i);
            Long total = countTotal(land, zooProperties, typeName);

            stringBuffer.append(String.format("Итого %s = %s; ", typeName, total));

            if(i > 0 && i % 5 == 0){
                stringBuffer.append("\r\n\t\t\t");
            }
        }

        stringBuffer.append("\r\n");
        stringBuffer.append("С начала времён:\r\n\t\t\t");

        Map<String, Long> actionCount = countActions(land, zooProperties);

        List<String> actions = new ArrayList<>(actionCount.keySet());

        for (int i = 0; i < actions.size(); i++) {

            String action = actions.get(i);

            stringBuffer.append(String.format("Действие %s повторилось раз: %s; ", action, actionCount.get(action)));

            if(i > 0 && (i + 1) % 2 == 0){
                stringBuffer.append("\r\n\t\t\t");
            }
        }

        stringBuffer.append("\r\n\r\n\r\n");

        System.out.println(stringBuffer.toString());
    }

    private Map<String, Long> countActions(Land land, ZooProperties zooProperties) {
        int landSizeY = Integer.parseInt(zooProperties.getProperty("land.size.y"));
        int landSizeX = Integer.parseInt(zooProperties.getProperty("land.size.x"));

        Map<String, Long> out = new HashMap<>();

        for (int posY = 0; posY < landSizeY; posY++) {
            for (int posX = 0; posX < landSizeX; posX++) {
                LandTile landTile = land.getIsland()[posY][posX];
                if(landTile.getTileHistory().size() > 0){
                    addTileInfo(landTile, out);
                }
            }
        }

        return out;
    }

    private void addTileInfo(LandTile landTile, Map<String, Long> out) {
        for(Map.Entry<String, Long> action : landTile.getTileHistory().entrySet()){
            if(out.containsKey(action.getKey())){
                long landCount = out.get(action.getKey()) + action.getValue();
                out.put(action.getKey(), landCount);
                continue;
            }

            out.put(action.getKey(), action.getValue());
        }
    }

    private Long countTotal(Land land, ZooProperties zooProperties, String typeName) {
        long out = 0;

        int landSizeY = Integer.parseInt(zooProperties.getProperty("land.size.y"));
        int landSizeX = Integer.parseInt(zooProperties.getProperty("land.size.x"));

        for (int posY = 0; posY < landSizeY; posY++) {
            for (int posX = 0; posX < landSizeX; posX++) {
                out += (long) land.getIsland()[posY][posX].getTileItems().get(typeName).size();
            }
        }

        return out;
    }
}
