package com.asrian.thDanmakuCraft.world.entity.danmaku;

import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.function.BooleanSupplier;

public class THTask {
    public LinkedList<BooleanSupplier> SUPPLIERS = Lists.newLinkedList();

    public void add(final Runnable runnable, final int delayedTick) {
        final int[] tickArray = {delayedTick};
        SUPPLIERS.add(() -> {
            if (--tickArray[0] < 0) {
                runnable.run();
                return true;
            }
            return false;
        });
    }

    public void tick(){
        this.SUPPLIERS.removeIf(BooleanSupplier::getAsBoolean);
    }
}
