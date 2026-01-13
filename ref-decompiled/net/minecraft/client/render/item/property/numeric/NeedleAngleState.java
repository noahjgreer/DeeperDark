/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.item.property.numeric.NeedleAngleState
 *  net.minecraft.client.render.item.property.numeric.NeedleAngleState$Angler
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.HeldItemContext
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.numeric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.NeedleAngleState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public abstract class NeedleAngleState {
    private final boolean wobble;

    protected NeedleAngleState(boolean wobble) {
        this.wobble = wobble;
    }

    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable HeldItemContext pos, int seed) {
        World world2;
        if (pos == null) {
            pos = stack.getHolder();
        }
        if (pos == null) {
            return 0.0f;
        }
        if (world == null && (world2 = pos.getEntityWorld()) instanceof ClientWorld) {
            ClientWorld clientWorld;
            world = clientWorld = (ClientWorld)world2;
        }
        if (world == null) {
            return 0.0f;
        }
        return this.getAngle(stack, world, seed, pos);
    }

    protected abstract float getAngle(ItemStack var1, ClientWorld var2, int var3, HeldItemContext var4);

    protected boolean hasWobble() {
        return this.wobble;
    }

    protected Angler createAngler(float speedMultiplier) {
        return this.wobble ? NeedleAngleState.createWobblyAngler((float)speedMultiplier) : NeedleAngleState.createInstantAngler();
    }

    public static Angler createWobblyAngler(float speedMultiplier) {
        return new /* Unavailable Anonymous Inner Class!! */;
    }

    public static Angler createInstantAngler() {
        return new /* Unavailable Anonymous Inner Class!! */;
    }
}

