/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tooltip.ProfilesTooltipComponent;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipState;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.RealmsCreateRealmScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class RealmsMainScreen.RealmSelectionListEntry
extends RealmsMainScreen.Entry {
    private static final Text ONLINE_PLAYERS_TEXT = Text.translatable("mco.onlinePlayers");
    private static final int field_52120 = 9;
    private static final int field_62084 = 3;
    private static final int field_32054 = 36;
    final RealmsServer server;
    private final TooltipState tooltip;

    public RealmsMainScreen.RealmSelectionListEntry(RealmsServer server) {
        super(RealmsMainScreen.this);
        this.tooltip = new TooltipState();
        this.server = server;
        boolean bl = RealmsMainScreen.isSelfOwnedServer(server);
        if (RealmsMainScreen.isSnapshotRealmsEligible() && bl && server.isPrerelease()) {
            this.tooltip.setTooltip(Tooltip.of(Text.translatable("mco.snapshot.paired", server.parentWorldName)));
        } else if (!bl && server.needsDowngrade()) {
            this.tooltip.setTooltip(Tooltip.of(Text.translatable("mco.snapshot.friendsRealm.downgrade", server.activeVersion)));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        if (this.server.state == RealmsServer.State.UNINITIALIZED) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, NEW_REALM_ICON_TEXTURE, this.getContentX() - 5, this.getContentMiddleY() - 10, 40, 20);
            int i = this.getContentMiddleY() - ((RealmsMainScreen)RealmsMainScreen.this).textRenderer.fontHeight / 2;
            context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, UNINITIALIZED_TEXT, this.getContentX() + 40 - 2, i, -8388737);
            return;
        }
        RealmsUtil.drawPlayerHead(context, this.getContentX(), this.getContentY(), 32, this.server.ownerUUID);
        this.drawServerNameAndVersion(context, this.getContentY(), this.getContentX(), this.getContentWidth(), -1, this.server);
        this.drawDescription(context, this.getContentY(), this.getContentX(), this.getContentWidth(), this.server);
        this.drawOwnerOrExpiredText(context, this.getContentY(), this.getContentX(), this.server);
        this.renderStatusIcon(this.server, context, this.getContentRightEnd(), this.getContentY(), mouseX, mouseY);
        boolean bl = this.drawPlayers(context, this.getContentY(), this.getContentX(), this.getContentWidth(), this.getContentHeight(), mouseX, mouseY, deltaTicks);
        if (!bl) {
            this.tooltip.render(context, mouseX, mouseY, hovered, this.isFocused(), new ScreenRect(this.getContentX(), this.getContentY(), this.getContentWidth(), this.getContentHeight()));
        }
    }

    private boolean drawPlayers(DrawContext context, int top, int left, int width, int height, int mouseX, int mouseY, float tickProgress) {
        List<ProfileComponent> list = RealmsMainScreen.this.onlinePlayers.get(this.server.id);
        int i = list.size();
        if (i > 0) {
            int j = left + width - 21;
            int k = top + height - 9 - 2;
            int l = 9 * i + 3 * (i - 1);
            int m = j - l;
            ArrayList<PlayerSkinCache.Entry> list2 = mouseX >= m && mouseX <= j && mouseY >= k && mouseY <= k + 9 ? new ArrayList<PlayerSkinCache.Entry>(i) : null;
            PlayerSkinCache playerSkinCache = RealmsMainScreen.this.client.getPlayerSkinCache();
            for (int n = 0; n < list.size(); ++n) {
                ProfileComponent profileComponent = list.get(n);
                PlayerSkinCache.Entry entry = playerSkinCache.get(profileComponent);
                int o = m + 12 * n;
                PlayerSkinDrawer.draw(context, entry.getTextures(), o, k, 9);
                if (list2 == null) continue;
                list2.add(entry);
            }
            if (list2 != null) {
                context.drawTooltip(RealmsMainScreen.this.textRenderer, List.of(ONLINE_PLAYERS_TEXT), Optional.of(new ProfilesTooltipComponent.ProfilesData(list2)), mouseX, mouseY);
                return true;
            }
        }
        return false;
    }

    private void play() {
        RealmsMainScreen.this.client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        RealmsMainScreen.play(this.server, RealmsMainScreen.this);
    }

    private void createRealm() {
        RealmsMainScreen.this.client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        RealmsCreateRealmScreen realmsCreateRealmScreen = new RealmsCreateRealmScreen(RealmsMainScreen.this, this.server, this.server.isPrerelease());
        RealmsMainScreen.this.client.setScreen(realmsCreateRealmScreen);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.server.state == RealmsServer.State.UNINITIALIZED) {
            this.createRealm();
        } else if (this.server.shouldAllowPlay() && doubled && this.isFocused()) {
            this.play();
        }
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.isEnterOrSpace()) {
            if (this.server.state == RealmsServer.State.UNINITIALIZED) {
                this.createRealm();
                return true;
            }
            if (this.server.shouldAllowPlay()) {
                this.play();
                return true;
            }
        }
        return super.keyPressed(input);
    }

    @Override
    public Text getNarration() {
        if (this.server.state == RealmsServer.State.UNINITIALIZED) {
            return UNINITIALIZED_BUTTON_NARRATION;
        }
        return Text.translatable("narrator.select", Objects.requireNonNullElse(this.server.name, "unknown server"));
    }

    public RealmsServer getRealmsServer() {
        return this.server;
    }
}
