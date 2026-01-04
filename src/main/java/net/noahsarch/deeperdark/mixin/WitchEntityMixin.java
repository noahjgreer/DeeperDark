package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.util.WitchCureAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(WitchEntity.class)
public abstract class WitchEntityMixin extends RaiderEntity implements WitchCureAccessor {

    protected WitchEntityMixin(EntityType<? extends RaiderEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private int deeperdark$conversionTimer;
    @Unique
    private UUID deeperdark$converterUuid;

    @Override
    public void deeperdark$setConversionTimer(int time) {
        this.deeperdark$conversionTimer = time;
    }

    @Override
    public int deeperdark$getConversionTimer() {
        return this.deeperdark$conversionTimer;
    }

    @Override
    public void deeperdark$setConverterUuid(UUID uuid) {
        this.deeperdark$converterUuid = uuid;
    }

    @Override
    public UUID deeperdark$getConverterUuid() {
        return this.deeperdark$converterUuid;
    }
}

