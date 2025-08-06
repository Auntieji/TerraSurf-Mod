package net.add.terrasurf.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {
    // A unique name for our keybind action
    public static final String KEY_CATEGORY_TERRASURF = "key.category.terrasurf";
    public static final String KEY_ACTIVATE_BOARD = "key.terrasurf.activate_board";

    // Create the actual KeyMapping object
    public static final KeyMapping ACTIVATE_BOARD_KEY = new KeyMapping(
            KEY_ACTIVATE_BOARD, // The name of the key's action
            KeyConflictContext.IN_GAME, // This key should only work when in the game world
            InputConstants.Type.KEYSYM, // We are binding to a keyboard key
            GLFW.GLFW_KEY_V, // The default key is 'V'
            KEY_CATEGORY_TERRASURF // The category name in the Controls menu
    );
}
