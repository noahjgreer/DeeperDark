/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.entity.SignText
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.SignBlockEntityRenderState
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SignBlockEntityRenderState
extends BlockEntityRenderState {
    public @Nullable SignText frontText;
    public @Nullable SignText backText;
    public int textLineHeight;
    public int maxTextWidth;
    public boolean filterText;
    public boolean renderTextOutline;
}

