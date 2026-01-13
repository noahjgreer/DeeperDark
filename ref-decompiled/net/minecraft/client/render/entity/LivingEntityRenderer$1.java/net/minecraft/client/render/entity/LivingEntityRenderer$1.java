/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
static class LivingEntityRenderer.1 {
    static final /* synthetic */ int[] field_18227;
    static final /* synthetic */ int[] field_4743;

    static {
        field_4743 = new int[AbstractTeam.VisibilityRule.values().length];
        try {
            LivingEntityRenderer.1.field_4743[AbstractTeam.VisibilityRule.ALWAYS.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LivingEntityRenderer.1.field_4743[AbstractTeam.VisibilityRule.NEVER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LivingEntityRenderer.1.field_4743[AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LivingEntityRenderer.1.field_4743[AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_18227 = new int[Direction.values().length];
        try {
            LivingEntityRenderer.1.field_18227[Direction.SOUTH.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LivingEntityRenderer.1.field_18227[Direction.WEST.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LivingEntityRenderer.1.field_18227[Direction.NORTH.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LivingEntityRenderer.1.field_18227[Direction.EAST.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
