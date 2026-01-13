/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.search;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.search.IdentifierSearcher;

@Environment(value=EnvType.CLIENT)
static class IdentifierSearcher.1
implements IdentifierSearcher<T> {
    IdentifierSearcher.1() {
    }

    @Override
    public List<T> searchNamespace(String namespace) {
        return List.of();
    }

    @Override
    public List<T> searchPath(String path) {
        return List.of();
    }
}
