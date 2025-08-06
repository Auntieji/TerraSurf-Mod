package net.add.terrasurf.sounds; // --- THIS LINE IS NOW CORRECT ---

import net.add.terrasurf.TerraSurfMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TerraSurfMod.MODID);

    public static final RegistryObject<SoundEvent> BOARD_MOUNT = registerSoundEvent("board.mount");
    public static final RegistryObject<SoundEvent> BOARD_DISMOUNT = registerSoundEvent("board.dismount");
    public static final RegistryObject<SoundEvent> BOARD_WATER_TOGGLE = registerSoundEvent("board.water.toggle");
    public static final RegistryObject<SoundEvent> BOARD_LAND_FAST = registerSoundEvent("board.land.fast");
    public static final RegistryObject<SoundEvent> BOARD_LAND_SLOW = registerSoundEvent("board.land.slow");
    public static final RegistryObject<SoundEvent> BOARD_LOOP_CLIMBING = registerSoundEvent("board.loop.climbing");
    public static final RegistryObject<SoundEvent> BOARD_LOOP_LAVA = registerSoundEvent("board.loop.lava");
    public static final RegistryObject<SoundEvent> BOARD_LOOP_WATER = registerSoundEvent("board.loop.water");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(TerraSurfMod.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
