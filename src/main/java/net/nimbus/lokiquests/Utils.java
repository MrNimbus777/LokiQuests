package net.nimbus.lokiquests;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    static Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
    public static String toColor(String str){
        Matcher match = pattern.matcher(str);
        while (match.find()) {
            String color = str.substring(match.start() + 1, match.end());
            str = str.replace("&" + color, ChatColor.of(color) + "");
            match = pattern.matcher(str);
        }
        return str.replace("&", "\u00a7");
    }
    public static String toPrefix(String s){
        return Vars.PREFIX+toColor(s);
    }


    public static ItemStack setTag(ItemStack item, String key, String value){
        try {
            Class<?> clazz = Class.forName("org.bukkit.craftbukkit." + LQuests.a.version + ".inventory.CraftItemStack");
            net.minecraft.world.item.ItemStack nmsItem = (net.minecraft.world.item.ItemStack) clazz.getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
            CompoundTag tag = nmsItem.hasTag() ? nmsItem.getTag() : new CompoundTag();
            tag.putString(key, value);
            nmsItem.setTag(tag);
            return (ItemStack) clazz.getMethod("asBukkitCopy", net.minecraft.world.item.ItemStack.class).invoke(null, nmsItem);
        } catch (Exception e) {
            e.printStackTrace();
            return item;
        }
    }
    public static String readTag(ItemStack item, String key){
        try {
            Class<?> clazz = Class.forName("org.bukkit.craftbukkit." + LQuests.a.version + ".inventory.CraftItemStack");
            net.minecraft.world.item.ItemStack nmsItem = (net.minecraft.world.item.ItemStack) clazz.getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
            CompoundTag tag = nmsItem.hasTag() ? nmsItem.getTag() : new CompoundTag();
            return tag.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    protected static class VoidGenerator  extends ChunkGenerator {

        @NotNull
        @Override
        public ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z, @NotNull ChunkGenerator.BiomeGrid biome) {
            return createChunkData(world);
        }
    }
}
