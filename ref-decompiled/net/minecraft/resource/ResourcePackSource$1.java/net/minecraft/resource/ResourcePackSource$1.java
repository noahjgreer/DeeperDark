/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.util.function.UnaryOperator;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.text.Text;

static class ResourcePackSource.1
implements ResourcePackSource {
    final /* synthetic */ UnaryOperator field_40049;
    final /* synthetic */ boolean field_40050;

    ResourcePackSource.1() {
        this.field_40049 = unaryOperator;
        this.field_40050 = bl;
    }

    @Override
    public Text decorate(Text packDisplayName) {
        return (Text)this.field_40049.apply(packDisplayName);
    }

    @Override
    public boolean canBeEnabledLater() {
        return this.field_40050;
    }
}
