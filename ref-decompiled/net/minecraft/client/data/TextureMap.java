/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.Block
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.data.TextureKey
 *  net.minecraft.client.data.TextureMap
 *  net.minecraft.item.Item
 *  net.minecraft.registry.Registries
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.data;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.data.TextureKey;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class TextureMap {
    private final Map<TextureKey, Identifier> entries = Maps.newHashMap();
    private final Set<TextureKey> inherited = Sets.newHashSet();

    public TextureMap put(TextureKey key, Identifier id) {
        this.entries.put(key, id);
        return this;
    }

    public TextureMap register(TextureKey key, Identifier id) {
        this.entries.put(key, id);
        this.inherited.add(key);
        return this;
    }

    public Stream<TextureKey> getInherited() {
        return this.inherited.stream();
    }

    public TextureMap copy(TextureKey parent, TextureKey child) {
        this.entries.put(child, (Identifier)this.entries.get(parent));
        return this;
    }

    public TextureMap inherit(TextureKey parent, TextureKey child) {
        this.entries.put(child, (Identifier)this.entries.get(parent));
        this.inherited.add(child);
        return this;
    }

    public Identifier getTexture(TextureKey key) {
        for (TextureKey textureKey = key; textureKey != null; textureKey = textureKey.getParent()) {
            Identifier identifier = (Identifier)this.entries.get(textureKey);
            if (identifier == null) continue;
            return identifier;
        }
        throw new IllegalStateException("Can't find texture for slot " + String.valueOf(key));
    }

    public TextureMap copyAndAdd(TextureKey key, Identifier id) {
        TextureMap textureMap = new TextureMap();
        textureMap.entries.putAll(this.entries);
        textureMap.inherited.addAll(this.inherited);
        textureMap.put(key, id);
        return textureMap;
    }

    public static TextureMap all(Block block) {
        Identifier identifier = TextureMap.getId((Block)block);
        return TextureMap.all((Identifier)identifier);
    }

    public static TextureMap texture(Block block) {
        Identifier identifier = TextureMap.getId((Block)block);
        return TextureMap.texture((Identifier)identifier);
    }

    public static TextureMap texture(Identifier id) {
        return new TextureMap().put(TextureKey.TEXTURE, id);
    }

    public static TextureMap all(Identifier id) {
        return new TextureMap().put(TextureKey.ALL, id);
    }

    public static TextureMap cross(Block block) {
        return TextureMap.of((TextureKey)TextureKey.CROSS, (Identifier)TextureMap.getId((Block)block));
    }

    public static TextureMap side(Block block) {
        return TextureMap.of((TextureKey)TextureKey.SIDE, (Identifier)TextureMap.getId((Block)block));
    }

    public static TextureMap crossAndCrossEmissive(Block block) {
        return new TextureMap().put(TextureKey.CROSS, TextureMap.getId((Block)block)).put(TextureKey.CROSS_EMISSIVE, TextureMap.getSubId((Block)block, (String)"_emissive"));
    }

    public static TextureMap cross(Identifier id) {
        return TextureMap.of((TextureKey)TextureKey.CROSS, (Identifier)id);
    }

    public static TextureMap plant(Block block) {
        return TextureMap.of((TextureKey)TextureKey.PLANT, (Identifier)TextureMap.getId((Block)block));
    }

    public static TextureMap plantAndCrossEmissive(Block block) {
        return new TextureMap().put(TextureKey.PLANT, TextureMap.getId((Block)block)).put(TextureKey.CROSS_EMISSIVE, TextureMap.getSubId((Block)block, (String)"_emissive"));
    }

    public static TextureMap plant(Identifier id) {
        return TextureMap.of((TextureKey)TextureKey.PLANT, (Identifier)id);
    }

    public static TextureMap rail(Block block) {
        return TextureMap.of((TextureKey)TextureKey.RAIL, (Identifier)TextureMap.getId((Block)block));
    }

    public static TextureMap rail(Identifier id) {
        return TextureMap.of((TextureKey)TextureKey.RAIL, (Identifier)id);
    }

    public static TextureMap wool(Block block) {
        return TextureMap.of((TextureKey)TextureKey.WOOL, (Identifier)TextureMap.getId((Block)block));
    }

    public static TextureMap flowerbed(Block block) {
        return new TextureMap().put(TextureKey.FLOWERBED, TextureMap.getId((Block)block)).put(TextureKey.STEM, TextureMap.getSubId((Block)block, (String)"_stem"));
    }

    public static TextureMap wool(Identifier id) {
        return TextureMap.of((TextureKey)TextureKey.WOOL, (Identifier)id);
    }

    public static TextureMap stem(Block block) {
        return TextureMap.of((TextureKey)TextureKey.STEM, (Identifier)TextureMap.getId((Block)block));
    }

    public static TextureMap stemAndUpper(Block stem, Block upper) {
        return new TextureMap().put(TextureKey.STEM, TextureMap.getId((Block)stem)).put(TextureKey.UPPERSTEM, TextureMap.getId((Block)upper));
    }

    public static TextureMap pattern(Block block) {
        return TextureMap.of((TextureKey)TextureKey.PATTERN, (Identifier)TextureMap.getId((Block)block));
    }

    public static TextureMap fan(Block block) {
        return TextureMap.of((TextureKey)TextureKey.FAN, (Identifier)TextureMap.getId((Block)block));
    }

    public static TextureMap crop(Identifier id) {
        return TextureMap.of((TextureKey)TextureKey.CROP, (Identifier)id);
    }

    public static TextureMap paneAndTopForEdge(Block block, Block top) {
        return new TextureMap().put(TextureKey.PANE, TextureMap.getId((Block)block)).put(TextureKey.EDGE, TextureMap.getSubId((Block)top, (String)"_top"));
    }

    public static TextureMap of(TextureKey key, Identifier id) {
        return new TextureMap().put(key, id);
    }

    public static TextureMap sideEnd(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId((Block)block, (String)"_side")).put(TextureKey.END, TextureMap.getSubId((Block)block, (String)"_top"));
    }

    public static TextureMap sideAndTop(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId((Block)block, (String)"_side")).put(TextureKey.TOP, TextureMap.getSubId((Block)block, (String)"_top"));
    }

    public static TextureMap pottedAzaleaBush(Block block) {
        return new TextureMap().put(TextureKey.PLANT, TextureMap.getSubId((Block)block, (String)"_plant")).put(TextureKey.SIDE, TextureMap.getSubId((Block)block, (String)"_side")).put(TextureKey.TOP, TextureMap.getSubId((Block)block, (String)"_top"));
    }

    public static TextureMap sideAndEndForTop(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getId((Block)block)).put(TextureKey.END, TextureMap.getSubId((Block)block, (String)"_top")).put(TextureKey.PARTICLE, TextureMap.getId((Block)block));
    }

    public static TextureMap sideEnd(Identifier side, Identifier end) {
        return new TextureMap().put(TextureKey.SIDE, side).put(TextureKey.END, end);
    }

    public static TextureMap textureSideTop(Block block) {
        return new TextureMap().put(TextureKey.TEXTURE, TextureMap.getId((Block)block)).put(TextureKey.SIDE, TextureMap.getSubId((Block)block, (String)"_side")).put(TextureKey.TOP, TextureMap.getSubId((Block)block, (String)"_top"));
    }

    public static TextureMap textureParticle(Block block) {
        return new TextureMap().put(TextureKey.TEXTURE, TextureMap.getId((Block)block)).put(TextureKey.PARTICLE, TextureMap.getSubId((Block)block, (String)"_particle"));
    }

    public static TextureMap sideTopBottom(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId((Block)block, (String)"_side")).put(TextureKey.TOP, TextureMap.getSubId((Block)block, (String)"_top")).put(TextureKey.BOTTOM, TextureMap.getSubId((Block)block, (String)"_bottom"));
    }

    public static TextureMap wallSideTopBottom(Block block) {
        Identifier identifier = TextureMap.getId((Block)block);
        return new TextureMap().put(TextureKey.WALL, identifier).put(TextureKey.SIDE, identifier).put(TextureKey.TOP, TextureMap.getSubId((Block)block, (String)"_top")).put(TextureKey.BOTTOM, TextureMap.getSubId((Block)block, (String)"_bottom"));
    }

    public static TextureMap wallSideEnd(Block block) {
        Identifier identifier = TextureMap.getId((Block)block);
        return new TextureMap().put(TextureKey.TEXTURE, identifier).put(TextureKey.WALL, identifier).put(TextureKey.SIDE, identifier).put(TextureKey.END, TextureMap.getSubId((Block)block, (String)"_top"));
    }

    public static TextureMap topBottom(Identifier top, Identifier bottom) {
        return new TextureMap().put(TextureKey.TOP, top).put(TextureKey.BOTTOM, bottom);
    }

    public static TextureMap topBottom(Block block) {
        return new TextureMap().put(TextureKey.TOP, TextureMap.getSubId((Block)block, (String)"_top")).put(TextureKey.BOTTOM, TextureMap.getSubId((Block)block, (String)"_bottom"));
    }

    public static TextureMap particle(Block block) {
        return new TextureMap().put(TextureKey.PARTICLE, TextureMap.getId((Block)block));
    }

    public static TextureMap particle(Identifier id) {
        return new TextureMap().put(TextureKey.PARTICLE, id);
    }

    public static TextureMap fire0(Block block) {
        return new TextureMap().put(TextureKey.FIRE, TextureMap.getSubId((Block)block, (String)"_0"));
    }

    public static TextureMap fire1(Block block) {
        return new TextureMap().put(TextureKey.FIRE, TextureMap.getSubId((Block)block, (String)"_1"));
    }

    public static TextureMap lantern(Block block) {
        return new TextureMap().put(TextureKey.LANTERN, TextureMap.getId((Block)block));
    }

    public static TextureMap torch(Block block) {
        return new TextureMap().put(TextureKey.TORCH, TextureMap.getId((Block)block));
    }

    public static TextureMap torch(Identifier id) {
        return new TextureMap().put(TextureKey.TORCH, id);
    }

    public static TextureMap trialSpawner(Block block, String side, String top) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId((Block)block, (String)side)).put(TextureKey.TOP, TextureMap.getSubId((Block)block, (String)top)).put(TextureKey.BOTTOM, TextureMap.getSubId((Block)block, (String)"_bottom"));
    }

    public static TextureMap vault(Block block, String front, String side, String top, String bottom) {
        return new TextureMap().put(TextureKey.FRONT, TextureMap.getSubId((Block)block, (String)front)).put(TextureKey.SIDE, TextureMap.getSubId((Block)block, (String)side)).put(TextureKey.TOP, TextureMap.getSubId((Block)block, (String)top)).put(TextureKey.BOTTOM, TextureMap.getSubId((Block)block, (String)bottom));
    }

    public static TextureMap particle(Item item) {
        return new TextureMap().put(TextureKey.PARTICLE, TextureMap.getId((Item)item));
    }

    public static TextureMap sideFrontBack(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId((Block)block, (String)"_side")).put(TextureKey.FRONT, TextureMap.getSubId((Block)block, (String)"_front")).put(TextureKey.BACK, TextureMap.getSubId((Block)block, (String)"_back"));
    }

    public static TextureMap sideFrontTopBottom(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId((Block)block, (String)"_side")).put(TextureKey.FRONT, TextureMap.getSubId((Block)block, (String)"_front")).put(TextureKey.TOP, TextureMap.getSubId((Block)block, (String)"_top")).put(TextureKey.BOTTOM, TextureMap.getSubId((Block)block, (String)"_bottom"));
    }

    public static TextureMap sideFrontTop(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId((Block)block, (String)"_side")).put(TextureKey.FRONT, TextureMap.getSubId((Block)block, (String)"_front")).put(TextureKey.TOP, TextureMap.getSubId((Block)block, (String)"_top"));
    }

    public static TextureMap sideFrontEnd(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId((Block)block, (String)"_side")).put(TextureKey.FRONT, TextureMap.getSubId((Block)block, (String)"_front")).put(TextureKey.END, TextureMap.getSubId((Block)block, (String)"_end"));
    }

    public static TextureMap top(Block top) {
        return new TextureMap().put(TextureKey.TOP, TextureMap.getSubId((Block)top, (String)"_top"));
    }

    public static TextureMap frontSideWithCustomBottom(Block block, Block bottom) {
        return new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId((Block)block, (String)"_front")).put(TextureKey.DOWN, TextureMap.getId((Block)bottom)).put(TextureKey.UP, TextureMap.getSubId((Block)block, (String)"_top")).put(TextureKey.NORTH, TextureMap.getSubId((Block)block, (String)"_front")).put(TextureKey.EAST, TextureMap.getSubId((Block)block, (String)"_side")).put(TextureKey.SOUTH, TextureMap.getSubId((Block)block, (String)"_side")).put(TextureKey.WEST, TextureMap.getSubId((Block)block, (String)"_front"));
    }

    public static TextureMap frontTopSide(Block frontTopSideBlock, Block downBlock) {
        return new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId((Block)frontTopSideBlock, (String)"_front")).put(TextureKey.DOWN, TextureMap.getId((Block)downBlock)).put(TextureKey.UP, TextureMap.getSubId((Block)frontTopSideBlock, (String)"_top")).put(TextureKey.NORTH, TextureMap.getSubId((Block)frontTopSideBlock, (String)"_front")).put(TextureKey.SOUTH, TextureMap.getSubId((Block)frontTopSideBlock, (String)"_front")).put(TextureKey.EAST, TextureMap.getSubId((Block)frontTopSideBlock, (String)"_side")).put(TextureKey.WEST, TextureMap.getSubId((Block)frontTopSideBlock, (String)"_side"));
    }

    public static TextureMap snifferEgg(String age) {
        return new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId((Block)Blocks.SNIFFER_EGG, (String)(age + "_north"))).put(TextureKey.BOTTOM, TextureMap.getSubId((Block)Blocks.SNIFFER_EGG, (String)(age + "_bottom"))).put(TextureKey.TOP, TextureMap.getSubId((Block)Blocks.SNIFFER_EGG, (String)(age + "_top"))).put(TextureKey.NORTH, TextureMap.getSubId((Block)Blocks.SNIFFER_EGG, (String)(age + "_north"))).put(TextureKey.SOUTH, TextureMap.getSubId((Block)Blocks.SNIFFER_EGG, (String)(age + "_south"))).put(TextureKey.EAST, TextureMap.getSubId((Block)Blocks.SNIFFER_EGG, (String)(age + "_east"))).put(TextureKey.WEST, TextureMap.getSubId((Block)Blocks.SNIFFER_EGG, (String)(age + "_west")));
    }

    public static TextureMap driedGhast(String hydration) {
        return new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId((Block)Blocks.DRIED_GHAST, (String)(hydration + "_north"))).put(TextureKey.BOTTOM, TextureMap.getSubId((Block)Blocks.DRIED_GHAST, (String)(hydration + "_bottom"))).put(TextureKey.TOP, TextureMap.getSubId((Block)Blocks.DRIED_GHAST, (String)(hydration + "_top"))).put(TextureKey.NORTH, TextureMap.getSubId((Block)Blocks.DRIED_GHAST, (String)(hydration + "_north"))).put(TextureKey.SOUTH, TextureMap.getSubId((Block)Blocks.DRIED_GHAST, (String)(hydration + "_south"))).put(TextureKey.EAST, TextureMap.getSubId((Block)Blocks.DRIED_GHAST, (String)(hydration + "_east"))).put(TextureKey.WEST, TextureMap.getSubId((Block)Blocks.DRIED_GHAST, (String)(hydration + "_west"))).put(TextureKey.TENTACLES, TextureMap.getSubId((Block)Blocks.DRIED_GHAST, (String)(hydration + "_tentacles")));
    }

    public static TextureMap campfire(Block block) {
        return new TextureMap().put(TextureKey.LIT_LOG, TextureMap.getSubId((Block)block, (String)"_log_lit")).put(TextureKey.FIRE, TextureMap.getSubId((Block)block, (String)"_fire"));
    }

    public static TextureMap candleCake(Block block, boolean lit) {
        return new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId((Block)Blocks.CAKE, (String)"_side")).put(TextureKey.BOTTOM, TextureMap.getSubId((Block)Blocks.CAKE, (String)"_bottom")).put(TextureKey.TOP, TextureMap.getSubId((Block)Blocks.CAKE, (String)"_top")).put(TextureKey.SIDE, TextureMap.getSubId((Block)Blocks.CAKE, (String)"_side")).put(TextureKey.CANDLE, TextureMap.getSubId((Block)block, (String)(lit ? "_lit" : "")));
    }

    public static TextureMap cauldron(Identifier content) {
        return new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId((Block)Blocks.CAULDRON, (String)"_side")).put(TextureKey.SIDE, TextureMap.getSubId((Block)Blocks.CAULDRON, (String)"_side")).put(TextureKey.TOP, TextureMap.getSubId((Block)Blocks.CAULDRON, (String)"_top")).put(TextureKey.BOTTOM, TextureMap.getSubId((Block)Blocks.CAULDRON, (String)"_bottom")).put(TextureKey.INSIDE, TextureMap.getSubId((Block)Blocks.CAULDRON, (String)"_inner")).put(TextureKey.CONTENT, content);
    }

    public static TextureMap sculkShrieker(boolean canSummon) {
        String string = canSummon ? "_can_summon" : "";
        return new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId((Block)Blocks.SCULK_SHRIEKER, (String)"_bottom")).put(TextureKey.SIDE, TextureMap.getSubId((Block)Blocks.SCULK_SHRIEKER, (String)"_side")).put(TextureKey.TOP, TextureMap.getSubId((Block)Blocks.SCULK_SHRIEKER, (String)"_top")).put(TextureKey.INNER_TOP, TextureMap.getSubId((Block)Blocks.SCULK_SHRIEKER, (String)(string + "_inner_top"))).put(TextureKey.BOTTOM, TextureMap.getSubId((Block)Blocks.SCULK_SHRIEKER, (String)"_bottom"));
    }

    public static TextureMap bars(Block block) {
        return new TextureMap().put(TextureKey.BARS, TextureMap.getId((Block)block)).put(TextureKey.EDGE, TextureMap.getId((Block)block));
    }

    public static TextureMap layer0(Item item) {
        return new TextureMap().put(TextureKey.LAYER0, TextureMap.getId((Item)item));
    }

    public static TextureMap layer0(Block block) {
        return new TextureMap().put(TextureKey.LAYER0, TextureMap.getId((Block)block));
    }

    public static TextureMap layer0(Identifier id) {
        return new TextureMap().put(TextureKey.LAYER0, id);
    }

    public static TextureMap layered(Identifier layer0, Identifier layer1) {
        return new TextureMap().put(TextureKey.LAYER0, layer0).put(TextureKey.LAYER1, layer1);
    }

    public static TextureMap layered(Identifier layer0, Identifier layer1, Identifier layer2) {
        return new TextureMap().put(TextureKey.LAYER0, layer0).put(TextureKey.LAYER1, layer1).put(TextureKey.LAYER2, layer2);
    }

    public static Identifier getId(Block block) {
        Identifier identifier = Registries.BLOCK.getId((Object)block);
        return identifier.withPrefixedPath("block/");
    }

    public static Identifier getSubId(Block block, String suffix) {
        Identifier identifier = Registries.BLOCK.getId((Object)block);
        return identifier.withPath(path -> "block/" + path + suffix);
    }

    public static Identifier getId(Item item) {
        Identifier identifier = Registries.ITEM.getId((Object)item);
        return identifier.withPrefixedPath("item/");
    }

    public static Identifier getSubId(Item item, String suffix) {
        Identifier identifier = Registries.ITEM.getId((Object)item);
        return identifier.withPath(path -> "item/" + path + suffix);
    }
}

