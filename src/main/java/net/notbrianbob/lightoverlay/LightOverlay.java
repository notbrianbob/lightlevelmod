package net.notbrianbob.lightoverlay;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

@Mod(LightOverlay.MOD_ID)
public class LightOverlay {
    public static final String MOD_ID = "lightoverlay";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static boolean isOverlayEnabled = false;
    private static KeyMapping lightOverlayToggleKey;

    public LightOverlay() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        lightOverlayToggleKey = new KeyMapping("key.lightoverlay.toggle", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F7, "key.categories.lightoverlay");
        // The RegisterKeyMappingsEvent is used to register the key mappings
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerKeyMappings);
        // Register LightLevel class to the event bus
        MinecraftForge.EVENT_BUS.register(LightLevel.class);
        MinecraftForge.EVENT_BUS.register(this);

        // Verify that the onClientTick method is being called
        MinecraftForge.EVENT_BUS.register(LightOverlay.class);
//        for (int i = 0; i < 100; i++) {
//            LOGGER.info("LightLevel class registered to the event bus.");
//        }
    }
    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(lightOverlayToggleKey);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (event.phase == ClientTickEvent.Phase.START) {
            //LOGGER.info("Client tick event triggered.");
            if (lightOverlayToggleKey.consumeClick()) {
                //LOGGER.info("Key was pressed.");
                isOverlayEnabled = !isOverlayEnabled;
                LOGGER.info("Overlay " + (isOverlayEnabled ? "enabled" : "disabled"));
            }
        }
    }
}
