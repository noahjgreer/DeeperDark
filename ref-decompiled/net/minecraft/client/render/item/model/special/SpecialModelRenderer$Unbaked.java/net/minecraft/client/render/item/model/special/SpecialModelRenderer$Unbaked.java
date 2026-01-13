/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static interface SpecialModelRenderer.Unbaked {
    public @Nullable SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext var1);

    public MapCodec<? extends SpecialModelRenderer.Unbaked> getCodec();
}
