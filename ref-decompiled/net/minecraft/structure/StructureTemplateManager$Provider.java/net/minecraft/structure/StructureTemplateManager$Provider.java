/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;

record StructureTemplateManager.Provider(Function<Identifier, Optional<StructureTemplate>> loader, Supplier<Stream<Identifier>> lister) {
}
