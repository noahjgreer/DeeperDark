/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.json.MultipartModelCombinedCondition
 *  net.minecraft.client.render.model.json.MultipartModelCombinedCondition$LogicalOperator
 *  net.minecraft.client.render.model.json.MultipartModelCondition
 *  net.minecraft.state.State
 *  net.minecraft.state.StateManager
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.MultipartModelCombinedCondition;
import net.minecraft.client.render.model.json.MultipartModelCondition;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;

@Environment(value=EnvType.CLIENT)
public record MultipartModelCombinedCondition(LogicalOperator operation, List<MultipartModelCondition> terms) implements MultipartModelCondition
{
    private final LogicalOperator operation;
    private final List<MultipartModelCondition> terms;

    public MultipartModelCombinedCondition(LogicalOperator operation, List<MultipartModelCondition> terms) {
        this.operation = operation;
        this.terms = terms;
    }

    public <O, S extends State<O, S>> Predicate<S> instantiate(StateManager<O, S> stateManager) {
        return this.operation.apply(Lists.transform((List)this.terms, condition -> condition.instantiate(stateManager)));
    }

    public LogicalOperator operation() {
        return this.operation;
    }

    public List<MultipartModelCondition> terms() {
        return this.terms;
    }
}

