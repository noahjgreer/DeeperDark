/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.PrintWriter;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;

@Environment(value=EnvType.CLIENT)
class GameOptions.3
implements GameOptions.Visitor {
    final /* synthetic */ PrintWriter field_28780;

    GameOptions.3() {
        this.field_28780 = printWriter;
    }

    public void print(String key) {
        this.field_28780.print(key);
        this.field_28780.print(':');
    }

    @Override
    public <T> void accept(String key, SimpleOption<T> option) {
        option.getCodec().encodeStart((DynamicOps)JsonOps.INSTANCE, option.getValue()).ifError(error -> LOGGER.error("Error saving option {}: {}", (Object)option, (Object)error.message())).ifSuccess(json -> {
            this.print(key);
            this.field_28780.println(GSON.toJson(json));
        });
    }

    @Override
    public int visitInt(String key, int current) {
        this.print(key);
        this.field_28780.println(current);
        return current;
    }

    @Override
    public boolean visitBoolean(String key, boolean current) {
        this.print(key);
        this.field_28780.println(current);
        return current;
    }

    @Override
    public String visitString(String key, String current) {
        this.print(key);
        this.field_28780.println(current);
        return current;
    }

    @Override
    public float visitFloat(String key, float current) {
        this.print(key);
        this.field_28780.println(current);
        return current;
    }

    @Override
    public <T> T visitObject(String key, T current, Function<String, T> decoder, Function<T, String> encoder) {
        this.print(key);
        this.field_28780.println(encoder.apply(current));
        return current;
    }
}
