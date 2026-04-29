package io.github.lucaargolo.seasons.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.lucaargolo.seasons.FabricSeasons;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.List;

import static io.github.lucaargolo.seasons.FabricSeasons.MOD_NAME;

public class CompatWarnState {

    private static CompatWarnState instance = null;

    private final Minecraft client;
    private final HashSet<ModInfo> availableCompatPacks;
    private final HashSet<String> alreadyWarned;
    private final HashSet<ModInfo> toWarn;
    private boolean dirty = false;

    private CompatWarnState(Minecraft client, HashSet<String> alreadyWarned) {
        this.client = client;
        this.availableCompatPacks = new HashSet<>();
        try {
            HttpClient http = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://gist.githubusercontent.com/lucaargolo/abfd0edbcf7340e6f8bf32698a8d2d57/raw/fabric-seasons-compat.json"))
                .GET()
                .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            JsonElement element = JsonParser.parseString(response.body());
            for (JsonElement modInfoElement : element.getAsJsonArray()) {
                availableCompatPacks.add(FabricSeasons.GSON.fromJson(modInfoElement, ModInfo.class));
            }
        } catch (Exception e) {
            FabricSeasons.LOGGER.error("[" + MOD_NAME + "] Failed to request compatibility mods list.", e);
        }
        this.alreadyWarned = alreadyWarned;
        this.toWarn = new HashSet<>();
        HashSet<String> availableNamespaces = new HashSet<>();
        ClientPacketListener handler = client.getConnection();
        if (handler != null) {
            handler.registryAccess().lookupOrThrow(Registries.BIOME).listElements().forEach(entry ->
                availableNamespaces.add(entry.key().identifier().getNamespace())
            );
            FabricSeasons.SEEDS_MAP.forEach((item, block) ->
                availableNamespaces.add(BuiltInRegistries.BLOCK.getKey(block).getNamespace())
            );
        }
        availableNamespaces.stream().filter(namespace -> !alreadyWarned.contains(namespace)).forEach(namespace ->
            availableCompatPacks.stream().filter(info -> info.mods.contains(namespace)).forEach(toWarn::add)
        );
    }

    private void saveState() {
        CompoundTag nbt = new CompoundTag();
        ListTag list = new ListTag();
        alreadyWarned.forEach(s -> list.add(StringTag.valueOf(s)));
        nbt.put("list", list);
        File compatWarnFile = new File(Minecraft.getInstance().gameDirectory, File.separator + "data" + File.separator + "seasons_compat_warn.nbt");
        try {
            Boolean ignored = compatWarnFile.getParentFile().mkdirs();
            Boolean ignored2 = compatWarnFile.createNewFile();
            NbtIo.writeCompressed(nbt, compatWarnFile.toPath());
        } catch (IOException e) {
            FabricSeasons.LOGGER.error("[" + MOD_NAME + "] Failed to save season compat warn state.", e);
        }
    }

    public static CompatWarnState loadState(Minecraft client) {
        File compatWarnFile = new File(Minecraft.getInstance().gameDirectory, File.separator + "data" + File.separator + "seasons_compat_warn.nbt");
        HashSet<String> alreadyWarned = new HashSet<>();
        CompoundTag nbt;
        try {
            nbt = NbtIo.readCompressed(compatWarnFile.toPath(), NbtAccounter.create(0x6400000L));
        } catch (Exception e) {
            nbt = new CompoundTag();
        }
        Tag rawTag = nbt.get("list");
        if (rawTag instanceof ListTag list) {
            list.forEach(listElement -> {
                if (listElement instanceof StringTag listString) {
                    alreadyWarned.add(listString.value());
                }
            });
        }
        return new CompatWarnState(client, alreadyWarned);
    }

    public static CompatWarnState getInstance(Minecraft client) {
        if (instance == null) {
            instance = loadState(client);
        }
        return instance;
    }

    public static void join(Minecraft client) {
        getInstance(client).join();
    }

    public void join() {
        toWarn.forEach(info -> {
            if (!alreadyWarned.contains(info.id) && !FabricLoader.getInstance().isModLoaded(info.id)) {
                LocalPlayer player = client.player;
                if (player != null) {
                    MutableComponent first, second;
                    if (!info.mods.contains("minecraft")) {
                        first = Component.literal("\n").append(Component.translatable("chat.seasons.mod_installed", Component.literal(info.name).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.YELLOW));
                        second = Component.literal(("\n§e" + Component.translatable("chat.seasons.compatibility").getString()).replace("Fabric Seasons", "§aFabric Seasons§e") + "\n");
                    } else {
                        first = Component.literal("\n").append(Component.translatable("chat.seasons.mod_not_installed", Component.literal(info.name).withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.YELLOW));
                        second = Component.literal(("\n§e" + Component.translatable("chat.seasons.extras").getString()) + "\n");
                    }
                    MutableComponent third = Component.literal("§e" + Component.translatable("chat.seasons.available_at").getString());
                    MutableComponent curse = Component.literal("§6§nCurseForge§r ").withStyle(s -> s.withClickEvent(new ClickEvent.OpenUrl(URI.create("https://www.curseforge.com/minecraft/mc-mods/" + info.url))));
                    MutableComponent modrinth = Component.literal("§2§nModrinth§r ").withStyle(s -> s.withClickEvent(new ClickEvent.OpenUrl(URI.create("https://modrinth.com/mod/" + info.url))));
                    MutableComponent github = Component.literal("§5§nGitHub§r\n").withStyle(s -> s.withClickEvent(new ClickEvent.OpenUrl(URI.create("https://github.com/lucaargolo/" + info.url + "/releases"))));
                    MutableComponent fourth = Component.literal("§e" + Component.translatable("chat.seasons.show_once").getString() + "\n");
                    player.sendSystemMessage(first.append(second).append(third).append(curse).append(modrinth).append(github).append(fourth));
                }
                alreadyWarned.add(info.id);
                dirty = true;
            }
        });
        if (dirty) {
            dirty = false;
            saveState();
        }
    }

    @SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
    public static class ModInfo {
        private List<String> mods;
        private String id;
        private String url;
        private String name;
    }
}
