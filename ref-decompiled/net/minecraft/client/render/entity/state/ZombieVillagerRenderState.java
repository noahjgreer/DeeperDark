/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.VillagerDataRenderState
 *  net.minecraft.client.render.entity.state.ZombieEntityRenderState
 *  net.minecraft.client.render.entity.state.ZombieVillagerRenderState
 *  net.minecraft.village.VillagerData
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.VillagerDataRenderState;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.village.VillagerData;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ZombieVillagerRenderState
extends ZombieEntityRenderState
implements VillagerDataRenderState {
    public @Nullable VillagerData villagerData;

    public @Nullable VillagerData getVillagerData() {
        return this.villagerData;
    }
}

