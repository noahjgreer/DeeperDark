/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.texture;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureStitcher;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class TextureStitcher.Slot<T extends TextureStitcher.Stitchable> {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private @Nullable List<TextureStitcher.Slot<T>> subSlots;
    private  @Nullable TextureStitcher.Holder<T> texture;

    public TextureStitcher.Slot(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean fit(TextureStitcher.Holder<T> holder) {
        if (this.texture != null) {
            return false;
        }
        int i = holder.width;
        int j = holder.height;
        if (i > this.width || j > this.height) {
            return false;
        }
        if (i == this.width && j == this.height) {
            this.texture = holder;
            return true;
        }
        if (this.subSlots == null) {
            this.subSlots = new ArrayList<TextureStitcher.Slot<T>>(1);
            this.subSlots.add(new TextureStitcher.Slot<T>(this.x, this.y, i, j));
            int k = this.width - i;
            int l = this.height - j;
            if (l > 0 && k > 0) {
                int n;
                int m = Math.max(this.height, k);
                if (m >= (n = Math.max(this.width, l))) {
                    this.subSlots.add(new TextureStitcher.Slot<T>(this.x, this.y + j, i, l));
                    this.subSlots.add(new TextureStitcher.Slot<T>(this.x + i, this.y, k, this.height));
                } else {
                    this.subSlots.add(new TextureStitcher.Slot<T>(this.x + i, this.y, k, j));
                    this.subSlots.add(new TextureStitcher.Slot<T>(this.x, this.y + j, this.width, l));
                }
            } else if (k == 0) {
                this.subSlots.add(new TextureStitcher.Slot<T>(this.x, this.y + j, i, l));
            } else if (l == 0) {
                this.subSlots.add(new TextureStitcher.Slot<T>(this.x + i, this.y, k, j));
            }
        }
        for (TextureStitcher.Slot<T> slot : this.subSlots) {
            if (!slot.fit(holder)) continue;
            return true;
        }
        return false;
    }

    public void addAllFilledSlots(TextureStitcher.SpriteConsumer<T> consumer, int padding) {
        if (this.texture != null) {
            consumer.load(this.texture.sprite, this.getX(), this.getY(), padding);
        } else if (this.subSlots != null) {
            for (TextureStitcher.Slot slot : this.subSlots) {
                slot.addAllFilledSlots(consumer, padding);
            }
        }
    }

    public String toString() {
        return "Slot{originX=" + this.x + ", originY=" + this.y + ", width=" + this.width + ", height=" + this.height + ", texture=" + String.valueOf(this.texture) + ", subSlots=" + String.valueOf(this.subSlots) + "}";
    }
}
