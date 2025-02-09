package mods.thecomputerizer.discord.serverstalker.util;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RandomHelper {
    
    private static Random rand;
    
    public static <T> T getElement(List<T> list) {
        if(Objects.isNull(rand)) rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }
}