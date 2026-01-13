/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumers
 *  net.minecraft.client.render.VertexConsumers$Dual
 *  net.minecraft.client.render.VertexConsumers$Union
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumers;

@Environment(value=EnvType.CLIENT)
public class VertexConsumers {
    public static VertexConsumer union() {
        throw new IllegalArgumentException();
    }

    public static VertexConsumer union(VertexConsumer first) {
        return first;
    }

    public static VertexConsumer union(VertexConsumer first, VertexConsumer second) {
        return new Dual(first, second);
    }

    public static VertexConsumer union(VertexConsumer ... delegates) {
        return new Union(delegates);
    }
}

