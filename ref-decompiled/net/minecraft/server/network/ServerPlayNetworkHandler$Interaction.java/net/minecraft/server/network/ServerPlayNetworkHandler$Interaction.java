/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.network;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

@FunctionalInterface
static interface ServerPlayNetworkHandler.Interaction {
    public ActionResult run(ServerPlayerEntity var1, Entity var2, Hand var3);
}
