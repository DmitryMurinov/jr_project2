package ru.javarush.project2.zoo.factory;

import ru.javarush.project2.zoo.model.*;
import ru.javarush.project2.zoo.util.ZooProperties;

public class TileItemFactory {

    public TileItem make(TileItem item, ZooProperties zooProperties){

        String typeName = item.getTypeName();

        if(ZooProperties.VEGETATION_TYPE_NAME.equals(typeName)){
            return new Plant(zooProperties.getItemsProps().get(typeName));
        }

        //If creature can eat vegetation, it's a herbivorous creature
        if(zooProperties.getEatChain().get(typeName).containsKey(ZooProperties.VEGETATION_TYPE_NAME)){
            return new Herbivorous(zooProperties.getItemsProps().get(typeName));
        }

        //If creature can't eat vegetation, it's a carnivorous creature
        if(!zooProperties.getEatChain().get(typeName).containsKey(ZooProperties.VEGETATION_TYPE_NAME)){
            return new Carnivorous(zooProperties.getItemsProps().get(typeName));
        }

        return null;
    }

    public TileItem make(String typeName, ZooProperties zooProperties){
        if(ZooProperties.VEGETATION_TYPE_NAME.equals(typeName)){
            return new Plant(zooProperties.getItemsProps().get(typeName));
        }

        //If creature can eat vegetation, it's a herbivorous creature
        if(zooProperties.getEatChain().get(typeName).containsKey(ZooProperties.VEGETATION_TYPE_NAME)){
            return new Herbivorous(zooProperties.getItemsProps().get(typeName));
        }

        //If creature can't eat vegetation, it's a carnivorous creature
        if(!zooProperties.getEatChain().get(typeName).containsKey(ZooProperties.VEGETATION_TYPE_NAME)){
            return new Carnivorous(zooProperties.getItemsProps().get(typeName));
        }

        return null;
    }

}
