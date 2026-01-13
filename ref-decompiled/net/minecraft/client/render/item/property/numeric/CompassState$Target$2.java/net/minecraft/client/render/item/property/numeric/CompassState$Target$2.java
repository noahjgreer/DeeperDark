/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.numeric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.CompassState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.math.GlobalPos;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
final class CompassState.Target.2
extends CompassState.Target {
    CompassState.Target.2(String string2) {
    }

    @Override
    public @Nullable GlobalPos getPosition(ClientWorld world, ItemStack stack, @Nullable HeldItemContext context) {
        LodestoneTrackerComponent lodestoneTrackerComponent = stack.get(DataComponentTypes.LODESTONE_TRACKER);
        return lodestoneTrackerComponent != null ? (GlobalPos)lodestoneTrackerComponent.target().orElse(null) : null;
    }
}
