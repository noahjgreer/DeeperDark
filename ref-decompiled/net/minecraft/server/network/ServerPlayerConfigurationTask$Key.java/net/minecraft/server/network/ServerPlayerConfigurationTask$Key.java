/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.network;

public record ServerPlayerConfigurationTask.Key(String id) {
    @Override
    public String toString() {
        return this.id;
    }
}
