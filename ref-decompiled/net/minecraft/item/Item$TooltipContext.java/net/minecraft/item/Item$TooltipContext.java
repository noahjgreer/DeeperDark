/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.item;

import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

public static interface Item.TooltipContext {
    public static final Item.TooltipContext DEFAULT = new Item.TooltipContext(){

        @Override
        public  @Nullable RegistryWrapper.WrapperLookup getRegistryLookup() {
            return null;
        }

        @Override
        public float getUpdateTickRate() {
            return 20.0f;
        }

        @Override
        public @Nullable MapState getMapState(MapIdComponent mapIdComponent) {
            return null;
        }

        @Override
        public boolean isDifficultyPeaceful() {
            return false;
        }
    };

    public  @Nullable RegistryWrapper.WrapperLookup getRegistryLookup();

    public float getUpdateTickRate();

    public @Nullable MapState getMapState(MapIdComponent var1);

    public boolean isDifficultyPeaceful();

    public static Item.TooltipContext create(final @Nullable World world) {
        if (world == null) {
            return DEFAULT;
        }
        return new Item.TooltipContext(){

            @Override
            public RegistryWrapper.WrapperLookup getRegistryLookup() {
                return world.getRegistryManager();
            }

            @Override
            public float getUpdateTickRate() {
                return world.getTickManager().getTickRate();
            }

            @Override
            public MapState getMapState(MapIdComponent mapIdComponent) {
                return world.getMapState(mapIdComponent);
            }

            @Override
            public boolean isDifficultyPeaceful() {
                return world.getDifficulty() == Difficulty.PEACEFUL;
            }
        };
    }

    public static Item.TooltipContext create(final RegistryWrapper.WrapperLookup registries) {
        return new Item.TooltipContext(){

            @Override
            public RegistryWrapper.WrapperLookup getRegistryLookup() {
                return registries;
            }

            @Override
            public float getUpdateTickRate() {
                return 20.0f;
            }

            @Override
            public @Nullable MapState getMapState(MapIdComponent mapIdComponent) {
                return null;
            }

            @Override
            public boolean isDifficultyPeaceful() {
                return false;
            }
        };
    }
}
