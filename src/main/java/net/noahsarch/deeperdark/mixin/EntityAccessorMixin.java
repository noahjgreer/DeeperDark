package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.duck.EntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public class EntityAccessorMixin implements EntityAccessor {

    @Shadow public World world;

    @Override
    public World deeperdark$getWorld() {
        return this.world;
    }
}

