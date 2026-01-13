/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.text;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.ClickEvent;

public record ClickEvent.ShowDialog(RegistryEntry<Dialog> dialog) implements ClickEvent
{
    public static final MapCodec<ClickEvent.ShowDialog> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Dialog.ENTRY_CODEC.fieldOf("dialog").forGetter(ClickEvent.ShowDialog::dialog)).apply((Applicative)instance, ClickEvent.ShowDialog::new));

    @Override
    public ClickEvent.Action getAction() {
        return ClickEvent.Action.SHOW_DIALOG;
    }
}
