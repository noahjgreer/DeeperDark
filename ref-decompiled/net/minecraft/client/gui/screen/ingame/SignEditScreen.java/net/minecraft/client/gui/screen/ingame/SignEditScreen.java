/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SignEditScreen
extends AbstractSignEditScreen {
    public static final float BACKGROUND_SCALE = 62.500004f;
    public static final float TEXT_SCALE_MULTIPLIER = 0.9765628f;
    private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628f, 0.9765628f, 0.9765628f);
    private  @Nullable Model.SinglePartModel model;

    public SignEditScreen(SignBlockEntity sign, boolean filtered, boolean bl) {
        super(sign, filtered, bl);
    }

    @Override
    protected void init() {
        super.init();
        boolean bl = this.blockEntity.getCachedState().getBlock() instanceof SignBlock;
        this.model = SignBlockEntityRenderer.createSignModel(this.client.getLoadedEntityModels(), this.signType, bl);
    }

    @Override
    protected float getYOffset() {
        return 90.0f;
    }

    @Override
    protected void renderSignBackground(DrawContext context) {
        if (this.model == null) {
            return;
        }
        int i = this.width / 2;
        int j = i - 48;
        int k = 66;
        int l = i + 48;
        int m = 168;
        context.addSign(this.model, 62.500004f, this.signType, j, 66, l, 168);
    }

    @Override
    protected Vector3f getTextScale() {
        return TEXT_SCALE;
    }
}
