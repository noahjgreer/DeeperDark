/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.io.InputStream;
import java.util.function.BiConsumer;
import net.minecraft.resource.InputSupplier;
import net.minecraft.util.Identifier;

@FunctionalInterface
public static interface ResourcePack.ResultConsumer
extends BiConsumer<Identifier, InputSupplier<InputStream>> {
}
