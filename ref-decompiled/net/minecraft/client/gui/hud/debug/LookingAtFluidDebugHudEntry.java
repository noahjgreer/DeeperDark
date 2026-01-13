/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.hud.debug.DebugHudEntry
 *  net.minecraft.client.gui.hud.debug.DebugHudLines
 *  net.minecraft.client.gui.hud.debug.LookingAtFluidDebugHudEntry
 *  net.minecraft.entity.Entity
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.registry.Registries
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.HitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.WorldChunk
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud.debug;

import java.util.ArrayList;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LookingAtFluidDebugHudEntry
implements DebugHudEntry {
    private static final Identifier SECTION_ID = Identifier.ofVanilla((String)"looking_at_fluid");

    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
        World world2;
        Entity entity = MinecraftClient.getInstance().getCameraEntity();
        Object object = world2 = SharedConstants.SHOW_SERVER_DEBUG_VALUES ? world : MinecraftClient.getInstance().world;
        if (entity == null || world2 == null) {
            return;
        }
        HitResult hitResult = entity.raycast(20.0, 0.0f, true);
        ArrayList<Object> list = new ArrayList<Object>();
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
            FluidState fluidState = world2.getFluidState(blockPos);
            list.add(String.valueOf(Formatting.UNDERLINE) + "Targeted Fluid: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
            list.add(String.valueOf(Registries.FLUID.getId((Object)fluidState.getFluid())));
            for (Map.Entry entry : fluidState.getEntries().entrySet()) {
                list.add(this.getFluidPropertyLine(entry));
            }
            fluidState.streamTags().map(tag -> "#" + String.valueOf(tag.id())).forEach(list::add);
        }
        lines.addLinesToSection(SECTION_ID, list);
    }

    private String getFluidPropertyLine(Map.Entry<Property<?>, Comparable<?>> propertyAndValue) {
        Property<?> property = propertyAndValue.getKey();
        Comparable<?> comparable = propertyAndValue.getValue();
        Object string = Util.getValueAsString(property, comparable);
        if (Boolean.TRUE.equals(comparable)) {
            string = String.valueOf(Formatting.GREEN) + (String)string;
        } else if (Boolean.FALSE.equals(comparable)) {
            string = String.valueOf(Formatting.RED) + (String)string;
        }
        return property.getName() + ": " + (String)string;
    }
}

