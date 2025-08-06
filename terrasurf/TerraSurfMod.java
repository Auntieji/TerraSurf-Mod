package net.add.terrasurf;

import com.mojang.logging.LogUtils;
import net.add.terrasurf.client.renderer.TerraSurferEntityRenderer;
import net.add.terrasurf.enchantment.FlowEnchantment;
import net.add.terrasurf.entity.TerraSurferEntity;
import net.add.terrasurf.event.ModEvents;
import net.add.terrasurf.item.TerraSurferBoardItem;
// We no longer need to import the models directly here
import net.add.terrasurf.network.Messages;
import net.add.terrasurf.sounds.ModSounds;
import net.add.terrasurf.util.KeyBinds;
import net.add.terrasurf.util.ModModelLayers; // NEW IMPORT
import net.add.terrasurf.util.ModTags;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(TerraSurfMod.MODID)
public class TerraSurfMod {
    public static final String MODID = "terrasurf";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);

    public static final RegistryObject<Item> TERRASURFERBOARD = ITEMS.register("terrasurfboard", TerraSurferBoardItem::new);

    public static final RegistryObject<EntityType<TerraSurferEntity>> TERRASURFER_ENTITY = ENTITY_TYPES.register("terrasurfer_entity",
            () -> EntityType.Builder.of(TerraSurferEntity::new, net.minecraft.world.entity.MobCategory.MISC)
                    .sized(1.0f, 0.2f).build("terrasurfer_entity"));

    public static final RegistryObject<Enchantment> FLOW = ENCHANTMENTS.register("flow",
            () -> new FlowEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR_HEAD, EquipmentSlot.HEAD));

    public static final RegistryObject<CreativeModeTab> TERRASURF_TAB = CREATIVE_MODE_TABS.register("terrasurf_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(TERRASURFERBOARD.get()))
                    .title(Component.translatable("creativetab.terrasurf"))
                    .displayItems((p, out) -> out.accept(TERRASURFERBOARD.get())).build());

    public TerraSurfMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        ENCHANTMENTS.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);

        ModTags.register();
        Messages.register();

        MinecraftForge.EVENT_BUS.register(new ModEvents());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientSetup {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(TERRASURFER_ENTITY.get(), TerraSurferEntityRenderer::new);
        }

        @SubscribeEvent
        public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            // --- CHANGE: All layer definitions are now registered with a single, clean method call ---
            ModModelLayers.register(event);
        }

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBinds.ACTIVATE_BOARD_KEY);
        }
    }
}


