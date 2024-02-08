package net.notbrianbob.lightoverlay;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = LightOverlay.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    // Configuration for the overlay type
    public static final ForgeConfigSpec.EnumValue<OverlayType> OVERLAY_TYPE;

    public static final ForgeConfigSpec.ConfigValue<String> LIGHT_OVERLAY_TOGGLE_KEY;

    static {
        // Inside your configuration setup
        LIGHT_OVERLAY_TOGGLE_KEY = BUILDER
                .comment("Key for toggling the Light Overlay")
                .define("lightOverlayToggleKey", "key.keyboard.f7"); // Default key as F7
    }
    static {
        // Define the overlay type with a default of COLORED_X
        OVERLAY_TYPE = BUILDER
                .comment("Defines the type of overlay to show on blocks. Options: COLORED_X, FULL_HIGHLIGHT")
                .defineEnum("overlayType", OverlayType.COLORED_X);

        BUILDER.comment("LightOverlay Settings").push("general");
        // Additional configuration options can be added here
        BUILDER.pop();
    }

    static final ForgeConfigSpec SPEC = BUILDER.build();

    // Enum to represent the overlay type options
    public enum OverlayType {
        COLORED_X, FULL_HIGHLIGHT
    }

    public static OverlayType overlayType;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        overlayType = OVERLAY_TYPE.get();
    }
}
