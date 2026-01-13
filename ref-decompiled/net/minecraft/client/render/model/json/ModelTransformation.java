/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.json.ModelTransformation
 *  net.minecraft.client.render.model.json.ModelTransformation$1
 *  net.minecraft.client.render.model.json.Transformation
 *  net.minecraft.item.ItemDisplayContext
 */
package net.minecraft.client.render.model.json;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.item.ItemDisplayContext;

@Environment(value=EnvType.CLIENT)
public record ModelTransformation(Transformation thirdPersonLeftHand, Transformation thirdPersonRightHand, Transformation firstPersonLeftHand, Transformation firstPersonRightHand, Transformation head, Transformation gui, Transformation ground, Transformation fixed, Transformation fixedFromBottom) {
    private final Transformation thirdPersonLeftHand;
    private final Transformation thirdPersonRightHand;
    private final Transformation firstPersonLeftHand;
    private final Transformation firstPersonRightHand;
    private final Transformation head;
    private final Transformation gui;
    private final Transformation ground;
    private final Transformation fixed;
    private final Transformation fixedFromBottom;
    public static final ModelTransformation NONE = new ModelTransformation(Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY);

    public ModelTransformation(Transformation thirdPersonLeftHand, Transformation thirdPersonRightHand, Transformation firstPersonLeftHand, Transformation firstPersonRightHand, Transformation head, Transformation gui, Transformation ground, Transformation fixed, Transformation fixedFromBottom) {
        this.thirdPersonLeftHand = thirdPersonLeftHand;
        this.thirdPersonRightHand = thirdPersonRightHand;
        this.firstPersonLeftHand = firstPersonLeftHand;
        this.firstPersonRightHand = firstPersonRightHand;
        this.head = head;
        this.gui = gui;
        this.ground = ground;
        this.fixed = fixed;
        this.fixedFromBottom = fixedFromBottom;
    }

    public Transformation getTransformation(ItemDisplayContext renderMode) {
        return switch (1.field_4313[renderMode.ordinal()]) {
            case 1 -> this.thirdPersonLeftHand;
            case 2 -> this.thirdPersonRightHand;
            case 3 -> this.firstPersonLeftHand;
            case 4 -> this.firstPersonRightHand;
            case 5 -> this.head;
            case 6 -> this.gui;
            case 7 -> this.ground;
            case 8 -> this.fixed;
            case 9 -> this.fixedFromBottom;
            default -> Transformation.IDENTITY;
        };
    }

    public Transformation thirdPersonLeftHand() {
        return this.thirdPersonLeftHand;
    }

    public Transformation thirdPersonRightHand() {
        return this.thirdPersonRightHand;
    }

    public Transformation firstPersonLeftHand() {
        return this.firstPersonLeftHand;
    }

    public Transformation firstPersonRightHand() {
        return this.firstPersonRightHand;
    }

    public Transformation head() {
        return this.head;
    }

    public Transformation gui() {
        return this.gui;
    }

    public Transformation ground() {
        return this.ground;
    }

    public Transformation fixed() {
        return this.fixed;
    }

    public Transformation fixedFromBottom() {
        return this.fixedFromBottom;
    }
}

