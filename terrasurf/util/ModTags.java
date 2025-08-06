package net.add.terrasurf.util;

import net.add.terrasurf.TerraSurfMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> CLIMBABLE = tag("climbable");

        // This is the helper method that creates the tag key.
        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(TerraSurfMod.MODID, name));
        }
    }

    // --- NEW METHOD ---
    // This is an empty method we can call to make sure this class is loaded by Java.
    // It's a common trick in Forge modding to ensure tags are registered properly.
    public static void register() {
        // We don't need to do anything in here, its existence is what matters.
    }
}
