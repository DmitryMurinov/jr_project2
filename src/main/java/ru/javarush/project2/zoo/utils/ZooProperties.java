package ru.javarush.project2.zoo.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Getter;
import ru.javarush.project2.zoo.model.TileItem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ZooProperties {

    public final static String VEGETATION_TYPE_NAME = "Растения";

    private static ZooProperties INSTANCE;

    private final static Properties LAND_PROPERTIES = new Properties();

    @Getter
    private static Map<String, TileItem> itemsProps;

    /**
     * Chances to catch and eat food
     */
    @Getter
    private static Map<String, Map<String, Integer>> eatChain = new HashMap<>();

    /**
     * Chances to be eaten by someone on same tile
     */
    @Getter
    private static Map<String, Map<String, Integer>> eatenChain = new HashMap<>();

    /**
     * Load land basic properties like size x, y, and vegetation growth speed
     */
    {
        Path path = Paths.get(Path.of("").toAbsolutePath().toString(),
                "/src/main/resources/world/default/land.properties");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile())))){
            LAND_PROPERTIES.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load flora and fauna common properties for type
     */
    private static void initItemsProps() {
        Path path = Paths.get(Path.of("").toAbsolutePath().toString(),
                "/src/main/resources/world/default/creatures.yaml");

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        List<TileItem> items = new ArrayList<>();

        try {
            items = mapper.readValue(path.toFile(), new TypeReference<>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        itemsProps = items.stream().collect(Collectors.toMap(TileItem::getTypeName, Function.identity()));
    }

    /**
     * Load eat by properties
     */
    private static void initEatTable() {
        Path path = Paths.get(Path.of("").toAbsolutePath().toString(),
                "/src/main/resources/world/default/food_chain.csv");

        try {
            List<String> propertiesList = Files.readAllLines(path);

            int x = propertiesList.size() + 1;
            int y = propertiesList.size();

            String[][] table = new String[y][x];

            for (int i = 0; i < y; i++) {
                table[i] = propertiesList.get(i).split(";");
            }

            for (int i = 1; i < y; i++) {
                Map<String, Integer> eatTable = new HashMap<>();
                for (int j = 1; j < x; j++) {
                    String value = table[i][j].trim();
                    if("-".equals(value) || "0".equals(value)){
                        continue;
                    }

                    Integer chanceToEat = Integer.valueOf(value);
                    eatTable.put(table[0][j].trim(), chanceToEat);
                }
                eatChain.put(table[i][0].trim(), eatTable);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Init eaten map, need for creatures to calculate danger before moving
     */
    private static void initEatenTable() {
        for(String creature : eatChain.keySet()){
            Map<String, Integer> eatenBy = new HashMap<>();

            for(String eater : eatChain.keySet()){
                if(eater.equals(creature)){
                    continue;
                }

                if(eatChain.get(eater).get(creature) != null){
                    eatenBy.put(eater, eatChain.get(eater).get(creature));
                }
            }

            if(eatenBy.size() > 0){
                eatenChain.put(creature, eatenBy);
            }
        }
    }

    private ZooProperties() {
        initItemsProps();
        initEatTable();
        initEatenTable();
    }

    public static ZooProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ZooProperties();
        }

        return INSTANCE;
    }

    public static String getProperty(String name) {
        return LAND_PROPERTIES.getProperty(name);
    }

}
