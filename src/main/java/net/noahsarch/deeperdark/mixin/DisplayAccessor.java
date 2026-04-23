package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.class)
public interface DisplayAccessor {
    @Invoker("setBillboardConstraints")
    void deeperdark$setBillboardConstraints(Display.BillboardConstraints constraints);

    @Invoker("setTransformationInterpolationDuration")
    void deeperdark$setTransformationInterpolationDuration(int duration);

    @Invoker("setTransformationInterpolationDelay")
    void deeperdark$setTransformationInterpolationDelay(int delay);
}
