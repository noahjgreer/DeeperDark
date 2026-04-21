package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.duck.EntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public class EntityAccessorMixin implements EntityAccessor {

    @Shadow public Level level;

    @Override
    public Level deeperdark$getWorld() {
        return this.level;
    }
}

