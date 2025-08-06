package net.add.terrasurf.util; // Or a new client package like net.add.terrasurf.client

import net.add.terrasurf.TerraSurfMod;
import net.add.terrasurf.model.TerraSurferArmorModel;
import net.add.terrasurf.model.TerraSurferModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class ModModelLayers {

    // --- Step 1: Define all your ModelLayerLocations here ---
    // This is the layer for your entity's main model.
    public static final ModelLayerLocation TERRASURFER_ENTITY_LAYER = new ModelLayerLocation(
            new ResourceLocation(TerraSurfMod.MODID, "terrasurfer"), "main");

    // This is the layer for your custom armor/helmet model.
    public static final ModelLayerLocation TERRASURFER_ARMOR_LAYER = new ModelLayerLocation(
            new ResourceLocation(TerraSurfMod.MODID, "terrasurfer_armor"), "main");

    // FUTURE: When you add a new model, you'll just add a new line here.
    // public static final ModelLayerLocation NEW_MODEL_LAYER = new ModelLayerLocation(...);


    // --- Step 2: Create a single method to register all layers ---
    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // Register the entity model
        event.registerLayerDefinition(TERRASURFER_ENTITY_LAYER, TerraSurferModel::createBodyLayer);

        // Register the armor model
        event.registerLayerDefinition(TERRASURFER_ARMOR_LAYER, TerraSurferArmorModel::createBodyLayer);

        // FUTURE: When you add a new model, you'll just add its registration here.
        // event.registerLayerDefinition(NEW_MODEL_LAYER, NewModel::createBodyLayer);
    }
}
