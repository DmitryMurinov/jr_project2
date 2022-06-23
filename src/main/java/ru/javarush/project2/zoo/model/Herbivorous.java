package ru.javarush.project2.zoo.model;

import com.ctc.wstx.exc.WstxOutputException;
import ru.javarush.project2.zoo.util.ZooProperties;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Herbivorous extends Animal {

    public Herbivorous(ItemProperties value) {
        super(value);
    }

    /**
     * Can be replaced with app property later (in that case static should be removed)
     * Determinate max ticks between feeding of Herbivorous. If feed less than ticks
     * provided, herbivorous will slowly starve to death. If less than ticks provided,
     * herbivorous will slowly regain health up to max cap.
     */
    private static final int MAX_TICKS_BETWEEN_FEEDING_NO_STARVE = 5;

    /**
     * Can be replaced with app property later (in that case static should be removed)
     * Determinate to move towards grass or from predators, based on fullness of creature.
     * Number recommendation: something 0.1 to 1.0.
     * Used for very basic feed or flee strategy.
     */
    private static final double LINE_BETWEEN_STARVATION_AND_PREDATORS_RISKS = 0.7;

    /**
     * Herbivorous lost food implementation
     * A bit complex calculations need to avoid of death for high max food creature & low plant weight combination issues
     */
    public synchronized void lostFood(ZooProperties zooProperties) {
        if(zooProperties.getItemsProps().get(getTypeName()).getMaxFood() == 0.0){
            return;
        }

        double plantWeight = zooProperties.getItemsProps().get(ZooProperties.VEGETATION_TYPE_NAME).getWeight();

        double maxFoodForType = zooProperties.getItemsProps().get(this.getTypeName()).getMaxFood();
        int worldStarveTick = Integer.parseInt(zooProperties.getProperty("creature.ticks.to.starve"));

        double lostFood = Math.min(plantWeight / MAX_TICKS_BETWEEN_FEEDING_NO_STARVE, maxFoodForType / worldStarveTick);

        setCurrentFood(getCurrentFood() - lostFood);
        if (getCurrentFood() <= 0) {
            die();
        }
    }


    /**
     * Herbivorous eat implementation
     */
    @Override
    public void eat(Land land, ZooProperties zooProperties) {
        if (isEatAlready()) {
            return;
        }

        List<TileItem> food = land.getIsland()[getPosY()][getPosX()].getTileItems().get(ZooProperties.VEGETATION_TYPE_NAME);

        if (food.size() == 0) {
            return;
        }

        List<TileItem> alive = food.stream().filter(TileItem::isAlive).collect(Collectors.toList());

        for (int i = 0; i < alive.size(); i++) {
            synchronized (alive.get(i)) {
                TileItem foodItem = alive.get(i);
                if (!foodItem.isAlive()) {
                    continue;
                }

                if (getCurrentFood() + foodItem.getWeight() < getMaxFood()) {
                    setCurrentFood(getCurrentFood() + foodItem.getWeight());
                } else {
                    setCurrentFood(getMaxFood());
                }

                foodItem.die();
                setEatAlready(true);
                return;
            }
        }
    }

    public synchronized void move(Land land, ZooProperties zooProperties) {
        List<AnimalView> posibilities = getSurroundings().getAsList().stream()
                .filter(s -> land.getIsland()[s.getPosY()][s.getPosX()].getTileItems().get(getTypeName()).size() < zooProperties.getItemsProps().get(getTypeName()).getMaxOnTileCount())
                .collect(Collectors.toList());

        if(posibilities.size() == 0){
            return;
        }

        while(posibilities.size() > 0) {
            if (getCurrentFood() < getMaxFood() * LINE_BETWEEN_STARVATION_AND_PREDATORS_RISKS) {
                AnimalView tileToMove = posibilities.stream()
                        .max(Comparator.comparingInt(t -> t.getLocalCount().get(ZooProperties.VEGETATION_TYPE_NAME)))
                        .get();

                boolean moveResult = moveToTitle(land, zooProperties, posibilities, tileToMove);
                if(moveResult){
                    return;
                }

            } else {
                AnimalView tileToMove = posibilities.stream()
                        .min(Comparator.comparing(t -> {
                            Map<String, Integer> predators = zooProperties.getEatenChain().get(getTypeName());
                            long dangerLevel = 0L;
                            for(Map.Entry<String, Integer> predator : predators.entrySet()){
                                dangerLevel += (long) t.getLocalCount().get(predator.getKey()) * predator.getValue();
                            }

                            return dangerLevel;
                        }))
                        .get();

                boolean moveResult = moveToTitle(land, zooProperties, posibilities, tileToMove);
                if(moveResult){
                    return;
                }
            }
        }
    }

}
