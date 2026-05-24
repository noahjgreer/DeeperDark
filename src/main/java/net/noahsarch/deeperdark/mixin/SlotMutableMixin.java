package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

// Strips `final` from Slot.x and Slot.y so SlotPositionAccessor's @Accessor setters
// can write to them at runtime. Java 17+ rejects PUTFIELD on final fields outside <init>.
@Mixin(Slot.class)
public abstract class SlotMutableMixin {
    @Shadow @Mutable public int x;
    @Shadow @Mutable public int y;
}
