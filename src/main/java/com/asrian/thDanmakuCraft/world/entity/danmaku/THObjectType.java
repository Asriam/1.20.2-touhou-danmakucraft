package com.asrian.thDanmakuCraft.world.entity.danmaku;

import com.asrian.thDanmakuCraft.registries.THDanmakuCraftRegistries;
import com.asrian.thDanmakuCraft.world.entity.EntityTHObjectContainer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class THObjectType<T extends THObject> {
    private final THObjectType.THObjectFactory<T> factory;

    public static ResourceLocation getKey(THObjectType<?> object) {
        return THDanmakuCraftRegistries.THOBJECT_TYPE.getKey(object);
    }

    public ResourceLocation getKey() {
        return THDanmakuCraftRegistries.THOBJECT_TYPE.getKey(this);
    }

    public static THObjectType<? extends THObject> getValue(ResourceLocation key){
        return THDanmakuCraftRegistries.THOBJECT_TYPE.getValue(key);
    }

    public Class<? extends THObject> getBaseClass() {
        return THObject.class;
    }

    @Nullable
    public T create(EntityTHObjectContainer container) {
        return this.factory.create(this, container);
    }

    public THObjectType(THObjectType.THObjectFactory<T> factory) {
        this.factory = factory;
    }

    public static class Builder<T extends THObject> {
        private final THObjectType.THObjectFactory<T> factory;

        public Builder(THObjectFactory<T> factory) {
            this.factory = factory;
        }

        public static <T extends THObject> Builder<T> of(THObjectType.THObjectFactory<T> factory) {
            return new THObjectType.Builder<>(factory);
        }

        public THObjectType<T> build() {
            return new THObjectType<>(this.factory);
        }
    }

    public interface THObjectFactory<T extends THObject> {
        T create(THObjectType<T> type, EntityTHObjectContainer container);
    }
}
