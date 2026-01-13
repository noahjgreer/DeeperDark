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
import net.minecraft.client.search.SuffixArray;

@Environment(value=EnvType.CLIENT)
static class IdentifierSearcher.2
implements IdentifierSearcher<T> {
    final /* synthetic */ SuffixArray field_39201;
    final /* synthetic */ SuffixArray field_39202;

    IdentifierSearcher.2(SuffixArray suffixArray, SuffixArray suffixArray2) {
        this.field_39201 = suffixArray;
        this.field_39202 = suffixArray2;
    }

    @Override
    public List<T> searchNamespace(String namespace) {
        return this.field_39201.findAll(namespace);
    }

    @Override
    public List<T> searchPath(String path) {
        return this.field_39202.findAll(path);
    }
}
