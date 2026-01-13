/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.loot.function;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.nbt.NbtElement;

record CopyNbtLootFunction.Operation(NbtPathArgumentType.NbtPath parsedSourcePath, NbtPathArgumentType.NbtPath parsedTargetPath, CopyNbtLootFunction.Operator operator) {
    public static final Codec<CopyNbtLootFunction.Operation> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)NbtPathArgumentType.NbtPath.CODEC.fieldOf("source").forGetter(CopyNbtLootFunction.Operation::parsedSourcePath), (App)NbtPathArgumentType.NbtPath.CODEC.fieldOf("target").forGetter(CopyNbtLootFunction.Operation::parsedTargetPath), (App)CopyNbtLootFunction.Operator.CODEC.fieldOf("op").forGetter(CopyNbtLootFunction.Operation::operator)).apply((Applicative)instance, CopyNbtLootFunction.Operation::new));

    public void execute(Supplier<NbtElement> itemNbtGetter, NbtElement sourceEntityNbt) {
        try {
            List<NbtElement> list = this.parsedSourcePath.get(sourceEntityNbt);
            if (!list.isEmpty()) {
                this.operator.merge(itemNbtGetter.get(), this.parsedTargetPath, list);
            }
        }
        catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{CopyNbtLootFunction.Operation.class, "sourcePath;targetPath;op", "parsedSourcePath", "parsedTargetPath", "operator"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CopyNbtLootFunction.Operation.class, "sourcePath;targetPath;op", "parsedSourcePath", "parsedTargetPath", "operator"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CopyNbtLootFunction.Operation.class, "sourcePath;targetPath;op", "parsedSourcePath", "parsedTargetPath", "operator"}, this, object);
    }
}
