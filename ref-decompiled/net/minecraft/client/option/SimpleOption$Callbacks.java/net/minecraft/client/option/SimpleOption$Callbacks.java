/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;

@Environment(value=EnvType.CLIENT)
static interface SimpleOption.Callbacks<T> {
    public Function<SimpleOption<T>, ClickableWidget> getWidgetCreator(SimpleOption.TooltipFactory<T> var1, GameOptions var2, int var3, int var4, int var5, Consumer<T> var6);

    public Optional<T> validate(T var1);

    public Codec<T> codec();
}
