/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.json.MultipartModelCondition
 *  net.minecraft.client.render.model.json.SimpleMultipartModelSelector
 *  net.minecraft.client.render.model.json.SimpleMultipartModelSelector$Terms
 *  net.minecraft.state.State
 *  net.minecraft.state.StateManager
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.Util
 *  net.minecraft.util.dynamic.Codecs
 *  org.slf4j.Logger
 */
package net.minecraft.client.render.model.json;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.MultipartModelCondition;
import net.minecraft.client.render.model.json.SimpleMultipartModelSelector;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public record SimpleMultipartModelSelector(Map<String, Terms> tests) implements MultipartModelCondition
{
    private final Map<String, Terms> tests;
    static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<SimpleMultipartModelSelector> CODEC = Codecs.nonEmptyMap((Codec)Codec.unboundedMap((Codec)Codec.STRING, (Codec)Terms.VALUE_CODEC)).xmap(SimpleMultipartModelSelector::new, SimpleMultipartModelSelector::tests);

    public SimpleMultipartModelSelector(Map<String, Terms> tests) {
        this.tests = tests;
    }

    public <O, S extends State<O, S>> Predicate<S> instantiate(StateManager<O, S> stateManager) {
        ArrayList list = new ArrayList(this.tests.size());
        this.tests.forEach((property, terms) -> list.add(SimpleMultipartModelSelector.init((StateManager)stateManager, (String)property, (Terms)terms)));
        return Util.allOf(list);
    }

    private static <O, S extends State<O, S>> Predicate<S> init(StateManager<O, S> stateManager, String property, Terms terms) {
        Property property2 = stateManager.getProperty(property);
        if (property2 == null) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Unknown property '%s' on '%s'", property, stateManager.getOwner()));
        }
        return terms.instantiate(stateManager.getOwner(), property2);
    }

    public Map<String, Terms> tests() {
        return this.tests;
    }
}

