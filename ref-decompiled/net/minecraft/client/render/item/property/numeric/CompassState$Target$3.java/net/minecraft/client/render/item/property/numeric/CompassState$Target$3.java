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
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.math.GlobalPos;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
final class CompassState.Target.3
extends CompassState.Target {
    CompassState.Target.3(String string2) {
    }

    @Override
    public GlobalPos getPosition(ClientWorld world, ItemStack stack, @Nullable HeldItemContext context) {
        return world.getSpawnPoint().globalPos();
    }
}
