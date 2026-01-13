/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.VillagerDataRenderState;
import net.minecraft.village.VillagerData;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class VillagerEntityRenderState
extends ItemHolderEntityRenderState
implements VillagerDataRenderState {
    public boolean headRolling;
    public @Nullable VillagerData villagerData;

    @Override
    public @Nullable VillagerData getVillagerData() {
        return this.villagerData;
    }
}
