/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.serialization.Codec
 */
package net.minecraft.loot.function;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.StringIdentifiable;

public static abstract sealed class CopyNbtLootFunction.Operator
extends Enum<CopyNbtLootFunction.Operator>
implements StringIdentifiable {
    public static final /* enum */ CopyNbtLootFunction.Operator REPLACE = new CopyNbtLootFunction.Operator("replace"){

        @Override
        public void merge(NbtElement itemNbt, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceNbts) throws CommandSyntaxException {
            targetPath.put(itemNbt, (NbtElement)Iterables.getLast(sourceNbts));
        }
    };
    public static final /* enum */ CopyNbtLootFunction.Operator APPEND = new CopyNbtLootFunction.Operator("append"){

        @Override
        public void merge(NbtElement itemNbt, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceNbts) throws CommandSyntaxException {
            List<NbtElement> list = targetPath.getOrInit(itemNbt, NbtList::new);
            list.forEach(foundNbt -> {
                if (foundNbt instanceof NbtList) {
                    sourceNbts.forEach(sourceNbt -> ((NbtList)foundNbt).add(sourceNbt.copy()));
                }
            });
        }
    };
    public static final /* enum */ CopyNbtLootFunction.Operator MERGE = new CopyNbtLootFunction.Operator("merge"){

        @Override
        public void merge(NbtElement itemNbt, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceNbts) throws CommandSyntaxException {
            List<NbtElement> list = targetPath.getOrInit(itemNbt, NbtCompound::new);
            list.forEach(foundNbt -> {
                if (foundNbt instanceof NbtCompound) {
                    sourceNbts.forEach(sourceNbt -> {
                        if (sourceNbt instanceof NbtCompound) {
                            ((NbtCompound)foundNbt).copyFrom((NbtCompound)sourceNbt);
                        }
                    });
                }
            });
        }
    };
    public static final Codec<CopyNbtLootFunction.Operator> CODEC;
    private final String name;
    private static final /* synthetic */ CopyNbtLootFunction.Operator[] field_17036;

    public static CopyNbtLootFunction.Operator[] values() {
        return (CopyNbtLootFunction.Operator[])field_17036.clone();
    }

    public static CopyNbtLootFunction.Operator valueOf(String string) {
        return Enum.valueOf(CopyNbtLootFunction.Operator.class, string);
    }

    public abstract void merge(NbtElement var1, NbtPathArgumentType.NbtPath var2, List<NbtElement> var3) throws CommandSyntaxException;

    CopyNbtLootFunction.Operator(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static /* synthetic */ CopyNbtLootFunction.Operator[] method_36795() {
        return new CopyNbtLootFunction.Operator[]{REPLACE, APPEND, MERGE};
    }

    static {
        field_17036 = CopyNbtLootFunction.Operator.method_36795();
        CODEC = StringIdentifiable.createCodec(CopyNbtLootFunction.Operator::values);
    }
}
