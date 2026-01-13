/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.advancement;

import java.util.Collection;
import net.minecraft.advancement.AdvancementRequirements;

public static interface AdvancementRequirements.CriterionMerger {
    public static final AdvancementRequirements.CriterionMerger AND = AdvancementRequirements::allOf;
    public static final AdvancementRequirements.CriterionMerger OR = AdvancementRequirements::anyOf;

    public AdvancementRequirements create(Collection<String> var1);
}
