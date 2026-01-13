/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.BakedGeometry
 *  net.minecraft.client.render.model.BakedQuad
 *  net.minecraft.util.math.Direction
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.model;

import java.lang.runtime.SwitchBootstraps;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BakedGeometry {
    public static final BakedGeometry EMPTY = new BakedGeometry(List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
    private final List<BakedQuad> allQuads;
    private final List<BakedQuad> sidelessQuads;
    private final List<BakedQuad> northQuads;
    private final List<BakedQuad> southQuads;
    private final List<BakedQuad> eastQuads;
    private final List<BakedQuad> westQuads;
    private final List<BakedQuad> upQuads;
    private final List<BakedQuad> downQuads;

    BakedGeometry(List<BakedQuad> allQuads, List<BakedQuad> sidelessQuads, List<BakedQuad> northQuads, List<BakedQuad> southQuads, List<BakedQuad> eastQuads, List<BakedQuad> westQuads, List<BakedQuad> upQuads, List<BakedQuad> downQuads) {
        this.allQuads = allQuads;
        this.sidelessQuads = sidelessQuads;
        this.northQuads = northQuads;
        this.southQuads = southQuads;
        this.eastQuads = eastQuads;
        this.westQuads = westQuads;
        this.upQuads = upQuads;
        this.downQuads = downQuads;
    }

    public List<BakedQuad> getQuads(@Nullable Direction side) {
        Direction direction = side;
        int n = 0;
        return switch (SwitchBootstraps.enumSwitch("enumSwitch", new Object[]{"NORTH", "SOUTH", "EAST", "WEST", "UP", "DOWN"}, (Direction)direction, n)) {
            default -> throw new MatchException(null, null);
            case -1 -> this.sidelessQuads;
            case 0 -> this.northQuads;
            case 1 -> this.southQuads;
            case 2 -> this.eastQuads;
            case 3 -> this.westQuads;
            case 4 -> this.upQuads;
            case 5 -> this.downQuads;
        };
    }

    public List<BakedQuad> getAllQuads() {
        return this.allQuads;
    }
}

