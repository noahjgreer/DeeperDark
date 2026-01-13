/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

public record SnbtOperation.Type(String id, int argCount) {
    @Override
    public String toString() {
        return this.id + "/" + this.argCount;
    }
}
