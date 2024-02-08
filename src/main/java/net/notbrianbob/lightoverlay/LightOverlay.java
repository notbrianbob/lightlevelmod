package net.notbrianbob.lightoverlay;

import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.notbrianbob.lightoverlay.LightLevel;

@Mod(LightOverlay.MOD_ID)
public class LightOverlay {
    private static boolean isOverlayEnabled = false; // Tracks the state of the overlay
    private static boolean keyWasPressed = false; // Tracks if the key was already pressed
    public static final String MOD_ID = "lightoverlay";
    private static final Logger LOGGER = LogUtils.getLogger();
    // Define the KeyMapping as a static field so, it can be accessed in the event handler

    public LightOverlay() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Common setup tasks
    }

    private static final KeyMapping lightOverlayToggleKey = new KeyMapping(
            "key.lightoverlay.toggle",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F7,
            "key.categories.lightoverlay"
    );

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            boolean isCurrentlyPressed = lightOverlayToggleKey.isDown();
            if (isCurrentlyPressed && !keyWasPressed) {
                // Toggle the overlay state only if the key was not already pressed
                isOverlayEnabled = !isOverlayEnabled;
                keyWasPressed = true; // Mark key as pressed

                if (isOverlayEnabled) {
                    // Logic to enable the overlay
                    LightLevel.checkLightLevelsAroundPlayer();
                } else {
                    // Logic to disable the overlay, if needed
                }
            } else if (!isCurrentlyPressed && keyWasPressed) {
                // Reset keyWasPressed if the key is released
                keyWasPressed = false;
            }
        }
    }
}