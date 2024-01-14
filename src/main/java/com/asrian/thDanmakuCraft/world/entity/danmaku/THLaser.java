package com.asrian.thDanmakuCraft.world.entity.danmaku;

import com.asrian.thDanmakuCraft.init.THObjectInit;
import com.asrian.thDanmakuCraft.world.entity.EntityTHObjectContainer;

public class THLaser extends THObject {

    public THLaser(THObjectType<THLaser> type, EntityTHObjectContainer container) {
        super(type, container);
    }

    public THLaser(EntityTHObjectContainer container){
        this(THObjectInit.TH_LASER.get(),container);
    }


}
