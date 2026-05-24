package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface SlotPositionAccessor {
    @Accessor("x")
    void deeperdark$setX(int x);

    @Accessor("y")
    void deeperdark$setY(int y);
}
