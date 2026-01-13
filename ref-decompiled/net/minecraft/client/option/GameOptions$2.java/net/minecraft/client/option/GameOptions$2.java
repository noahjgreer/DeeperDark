/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.gson.JsonElement
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.option;

import com.google.common.base.MoreObjects;
import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.LenientJsonParser;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class GameOptions.2
implements GameOptions.Visitor {
    final /* synthetic */ NbtCompound field_28778;

    GameOptions.2() {
        this.field_28778 = nbtCompound;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private @Nullable String find(String key) {
        NbtElement nbtElement = this.field_28778.get(key);
        if (nbtElement == null) {
            return null;
        }
        if (!(nbtElement instanceof NbtString)) throw new IllegalStateException("Cannot read field of wrong type, expected string: " + String.valueOf(nbtElement));
        NbtString nbtString = (NbtString)nbtElement;
        try {
            String string = nbtString.value();
            return string;
        }
        catch (Throwable throwable) {
            throw new MatchException(throwable.toString(), throwable);
        }
    }

    @Override
    public <T> void accept(String key, SimpleOption<T> option) {
        String string = this.find(key);
        if (string != null) {
            JsonElement jsonElement = LenientJsonParser.parse(string.isEmpty() ? "\"\"" : string);
            option.getCodec().parse((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement).ifError(error -> LOGGER.error("Error parsing option value {} for option {}: {}", new Object[]{string, option, error.message()})).ifSuccess(option::setValue);
        }
    }

    @Override
    public int visitInt(String key, int current) {
        String string = this.find(key);
        if (string != null) {
            try {
                return Integer.parseInt(string);
            }
            catch (NumberFormatException numberFormatException) {
                LOGGER.warn("Invalid integer value for option {} = {}", new Object[]{key, string, numberFormatException});
            }
        }
        return current;
    }

    @Override
    public boolean visitBoolean(String key, boolean current) {
        String string = this.find(key);
        return string != null ? GameOptions.isTrue(string) : current;
    }

    @Override
    public String visitString(String key, String current) {
        return (String)MoreObjects.firstNonNull((Object)this.find(key), (Object)current);
    }

    @Override
    public float visitFloat(String key, float current) {
        String string = this.find(key);
        if (string != null) {
            if (GameOptions.isTrue(string)) {
                return 1.0f;
            }
            if (GameOptions.isFalse(string)) {
                return 0.0f;
            }
            try {
                return Float.parseFloat(string);
            }
            catch (NumberFormatException numberFormatException) {
                LOGGER.warn("Invalid floating point value for option {} = {}", new Object[]{key, string, numberFormatException});
            }
        }
        return current;
    }

    @Override
    public <T> T visitObject(String key, T current, Function<String, T> decoder, Function<T, String> encoder) {
        String string = this.find(key);
        return string == null ? current : decoder.apply(string);
    }
}
