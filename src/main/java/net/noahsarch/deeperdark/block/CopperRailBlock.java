package net.noahsarch.deeperdark.block;

import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.Vec3;

/** A copper rail at a fixed weather stage; waxed rails use this class directly. */
public class CopperRailBlock extends RailBlock {
    private final WeatheringCopper.WeatherState weatherState;

    public CopperRailBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties) {
        super(properties);
        this.weatherState = weatherState;
    }

    public WeatheringCopper.WeatherState getAge() {
        return this.weatherState;
    }

    /** Extra per-tick drag on top of vanilla's slowdown (0.997 for a ridden cart). */
    public static Vec3 applyDrag(AbstractMinecart minecart, Vec3 movement) {
        if (!(minecart.level().getBlockState(minecart.getCurrentBlockPosOrRailBelow()).getBlock() instanceof CopperRailBlock rail)) {
            return movement;
        }
        // Coasting distance for a ridden cart vs iron rails: 100%, ~38%, ~17%, ~7%.
        double keep = switch (rail.weatherState) {
            case UNAFFECTED -> 1.0;
            case EXPOSED -> 0.995;
            case WEATHERED -> 0.985;
            case OXIDIZED -> 0.96;
        };
        return movement.multiply(keep, 1.0, keep);
    }
}
