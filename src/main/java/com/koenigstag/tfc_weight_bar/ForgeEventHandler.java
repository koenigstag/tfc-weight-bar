package com.koenigstag.tfc_weight_bar;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import net.dries007.tfc.config.TFCConfig;

import com.koenigstag.tfc_curios_weight.CuriosHelpers;

public final class ForgeEventHandler {

  public static void init() {
    final IEventBus bus = MinecraftForge.EVENT_BUS;

    bus.addListener(ForgeEventHandler::onPlayerTick);
  }

  public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (Config.enableModCalculations == false) {
      return;
    }

    // When facing up in the rain, player slowly recovers thirst.
    final Player player = event.player;
    final Level level = player.level();

    // WeightBar calculations onPlayerTick
    if (!level.isClientSide() && !player.getAbilities().invulnerable && TFCConfig.SERVER.enableOverburdening.get()
        && level.getGameTime() % Config.calculateWeightEachNTicks == 0) {
      final int hugeHeavyCount = CuriosHelpers.countOverburdened(player);

      final int playerInvWeight = WeightBarHelpers.calculatePlayerFullWeight(player);
      final int maxInvWeight = WeightBarHelpers.getMaxInventoryWeight(player);

      // set current weight do display
      WeightBarGUI.changeCurrentWeight(playerInvWeight);

      Constants.debug("Huge Heavy Count: " + hugeHeavyCount);

      boolean isOverburdened = getIsOverburdened(playerInvWeight, maxInvWeight, hugeHeavyCount);

      if (isOverburdened) {
        WeightBarGUI.setIsOverburdened(true);

        // since player cannot move when overburdened - do not add exhaustion to food
        // data

        if (Config.enableWeightDebuffs) {
          player.addEffect(WeightBarHelpers.getOverburdened(false));
        }

        return;
      }

      boolean isExhausted = getIsExhausted(playerInvWeight, maxInvWeight, hugeHeavyCount);

      if (isExhausted) {
        WeightBarGUI.setIsExhausted(true);

        // Add exhaustion to food data
        if (Config.enableFoodExhaustion) {
          addPlayerFoodExhaustion(player, playerInvWeight, maxInvWeight);
        }

        if (Config.enableWeightDebuffs) {
          player.addEffect(WeightBarHelpers.getExhausted(false));
        }

        return;
      }

      // if not overburdened or exhausted, reset flags
      WeightBarGUI.resetBooleanFlags();
    }
  }

  private static boolean getIsExhausted(int playerInvWeight, int maxInvWeight, int hugeHeavyCount) {
    return WeightBarHelpers.isPlayerExhausted(playerInvWeight, maxInvWeight) || hugeHeavyCount >= 1;
  }

  private static boolean getIsOverburdened(int playerInvWeight, int maxInvWeight, int hugeHeavyCount) {
    return WeightBarHelpers.isPlayerOverburdened(playerInvWeight, maxInvWeight)
        || hugeHeavyCount >= 2;
  }

  private static void addPlayerFoodExhaustion(Player player, int playerInvWeight, int maxInvWeight) {
    final int overburdened = playerInvWeight - maxInvWeight;
    final int overburdenedPercent = (int) Mth.clamp(overburdened / (float) maxInvWeight * 100, 0, 100);

    if (overburdenedPercent > 0) {
      player.getFoodData().addExhaustion(overburdenedPercent * 0.01f);
    } else {
      player.getFoodData().addExhaustion(0.01f);
    }
  }
}
