/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud.debug;

import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LookingAtEntityDebugHudEntry
implements DebugHudEntry {
    private static final Identifier SECTION_ID = Identifier.ofVanilla("looking_at_entity");

    @Override
    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Entity entity = minecraftClient.targetedEntity;
        ArrayList<String> list = new ArrayList<String>();
        if (entity != null) {
            list.add(String.valueOf(Formatting.UNDERLINE) + "Targeted Entity");
            list.add(String.valueOf(Registries.ENTITY_TYPE.getId(entity.getType())));
        }
        lines.addLinesToSection(SECTION_ID, list);
    }
}
