package com.koenigstag.tfc_weight_bar;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.ISlotType;

import net.dries007.tfc.common.capabilities.size.Weight;
import net.dries007.tfc.common.capabilities.size.Size;

import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
        private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        private static final ForgeConfigSpec.BooleanValue ENABLE_MOD = BUILDER
                        .comment("Enable this mod. Default: true")
                        .define("enable_weight_calculations", true);

        private static final ForgeConfigSpec.BooleanValue ENABLE_UI_BAR = BUILDER
                        .comment("Enable weight bar on UI. Default: true")
                        .define("enable_ui_bar", true);

        private static final ForgeConfigSpec.BooleanValue ENABLE_WEIGHT_DEBUFFS = BUILDER
                        .comment("Whether to enable weight debuffs. Default: true")
                        .define("enable_weight_debuffs", true);

        public static final ForgeConfigSpec.BooleanValue ENABLE_FOOD_EXHAUSTION = BUILDER
                        .comment("Whether to enable food exhaustion coefficient. Default: true")
                        .define("enable_food_exhaustion", true);

        public static final ForgeConfigSpec.IntValue CALCULATE_WEIGHT_EACH_N_TICKS = BUILDER
                        .comment("Calculate weight every N ticks. Default: 20 ticks = 1 sec")
                        .defineInRange("calculate_weight_each_n_ticks", 20, 1, Integer.MAX_VALUE);

        private static final ForgeConfigSpec.IntValue MAX_INV_WEIGHT = BUILDER
                        .comment("Maximum inventory weight. Default: 1000")
                        .defineInRange("max_inv_weight", 1000, 0, Integer.MAX_VALUE);

        private static final ForgeConfigSpec.IntValue EXHAUSTED_WEIGHT_PERCENTAGE = BUILDER
                        .comment("Percentage of max weight that is considered exhausted. Default: 80")
                        .defineInRange("exhausted_weight_percentage", 80, 0, 100);

        private static final ForgeConfigSpec.IntValue VERY_LIGHT_WEIGHT = BUILDER
                        .comment("Weight of very light items. Default: 1")
                        .defineInRange("very_light_item_weight", 1, 0, Integer.MAX_VALUE);

        private static final ForgeConfigSpec.IntValue LIGHT_WEIGHT = BUILDER
                        .comment("Weight of very light items. Default: 4")
                        .defineInRange("light_item_weight", 4, 0, Integer.MAX_VALUE);

        private static final ForgeConfigSpec.IntValue MEDIUM_WEIGHT = BUILDER
                        .comment("Weight of medium items. Default: 16")
                        .defineInRange("medium_item_weight", 16, 0, Integer.MAX_VALUE);

        private static final ForgeConfigSpec.IntValue HEAVY_WEIGHT = BUILDER
                        .comment("Weight of medium items. Default: 32")
                        .defineInRange("heavy_item_weight", 32, 0, Integer.MAX_VALUE);

        private static final ForgeConfigSpec.IntValue VERY_HEAVY_WEIGHT = BUILDER
                        .comment("Weight of medium items. Default: 64")
                        .defineInRange("very_heavy_item_weight", 64, 0, Integer.MAX_VALUE);

        private static final ForgeConfigSpec.ConfigValue<List<? extends String>> SLOT_NAME_STRINGS = BUILDER
                        .comment("WIP. A list of slots to check in weight calculation.")
                        .defineListAllowEmpty("curios_slots", List.of(), Config::validateSlotName);

        static final ForgeConfigSpec SPEC = BUILDER.build();


        public static boolean enableModCalculations;
        public static boolean enableUIBar;
        public static boolean debugMode = true;
        public static List<ISlotType> curios_slots;
        public static int maxInvWeight;
        public static int exhaustedWeightPercentage;
        public static int calculateWeightEachNTicks;
        public static boolean enableWeightDebuffs;
        public static boolean enableFoodExhaustion;

        @SubscribeEvent
        static void onLoad(final ModConfigEvent event) {
                enableModCalculations = ENABLE_MOD.get();

                enableUIBar = ENABLE_UI_BAR.get();

                curios_slots = SLOT_NAME_STRINGS.get().stream()
                                .map(slotId -> CuriosApi.getSlot(slotId, getIsClient()).get())
                                .collect(Collectors.toList());

                calculateWeightEachNTicks = CALCULATE_WEIGHT_EACH_N_TICKS.get();

                maxInvWeight = MAX_INV_WEIGHT.get();

                exhaustedWeightPercentage = EXHAUSTED_WEIGHT_PERCENTAGE.get();

                enableWeightDebuffs = ENABLE_WEIGHT_DEBUFFS.get();

                enableFoodExhaustion = ENABLE_FOOD_EXHAUSTION.get();
        }


        public static int getWeightIntConfig(Weight weight) {
                /*  From TFC Weight Enum
                    Default values are:
                    VERY_LIGHT(64), // 1 points
                    LIGHT(32), // 4 points
                    MEDIUM(16), // 16 points
                    HEAVY(4), // 32 points
                    VERY_HEAVY(1); // 64 points
                */
                switch (weight) {
                        case VERY_LIGHT:
                                return VERY_LIGHT_WEIGHT.get();
                        case LIGHT:
                                return LIGHT_WEIGHT.get();
                        case MEDIUM:
                                return MEDIUM_WEIGHT.get();
                        case HEAVY:
                                return HEAVY_WEIGHT.get();
                        case VERY_HEAVY:
                                return VERY_HEAVY_WEIGHT.get();
                        default:
                                // any other item defaults to this int
                                return VERY_LIGHT_WEIGHT.get();
                }
        }

        public static int getSizeIntConfig(Size size) {
                return 0;
        }

        public static double getExhaustedWeightCoefficient() {
                return (double) exhaustedWeightPercentage / (double) 100;
        }

        public static String getNormalBarColor() {
                return "gray";
        }

        public static String getExhaustedBarColor() {
                return "pink";
        }

        public static String getOverburdenedBarColor() {
                return "red";
        }

        private static boolean getIsClient() {
                // TODO fix this. Needed in CuriosApi.getSlot(String id, boolean isClient)
                return false;
        }

        private static boolean validateSlotName(final Object obj) {
                return obj instanceof final String slotId
                                && CuriosApi.getSlot(slotId, getIsClient()).isPresent();
        }
}
