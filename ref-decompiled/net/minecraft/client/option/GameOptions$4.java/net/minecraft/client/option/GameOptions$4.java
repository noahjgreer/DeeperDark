/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;

@Environment(value=EnvType.CLIENT)
class GameOptions.4
implements GameOptions.OptionVisitor {
    final /* synthetic */ List field_49106;

    GameOptions.4() {
        this.field_49106 = list;
    }

    @Override
    public <T> void accept(String key, SimpleOption<T> option) {
        this.field_49106.add(Pair.of((Object)key, option.getValue()));
    }
}
