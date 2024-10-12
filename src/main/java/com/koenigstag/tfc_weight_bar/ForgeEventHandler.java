package com.koenigstag.tfc_weight_bar;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

import com.koenigstag.tfc_curios_weight.CuriosHelpers;

public final class ForgeEventHandler {

  public static void init() {
    final IEventBus bus = MinecraftForge.EVENT_BUS;

    bus.addListener(ForgeEventHandler::onPlayerTick);
  }

  public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
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

      boolean isOverburdened = WeightBarHelpers.isPlayerOverburdened(playerInvWeight, maxInvWeight)
          || hugeHeavyCount >= 2;

      if (isOverburdened) {
        // if overburdened, set flag
        WeightBarGUI.setIsOverburdened(true);

        // since player cannot move when overburdened - do not add exhaustion to food
        // data

        // add player debuff
        player.addEffect(WeightBarHelpers.getOverburdened(false));

        return;
      }

      boolean isExhausted = WeightBarHelpers.isPlayerExhausted(playerInvWeight, maxInvWeight) || hugeHeavyCount >= 1;

      if (isExhausted) {
        // if overburdened, set flag
        WeightBarGUI.setIsExhausted(true);

        // Add exhaustion to food data
        final int overburdened = playerInvWeight - maxInvWeight;
        final int overburdenedPercent = (int) Mth.clamp(overburdened / (float) maxInvWeight * 100, 0, 100);

        if (overburdenedPercent > 0) {
          player.getFoodData().addExhaustion(overburdenedPercent * 0.01f);
        }

        // add player debuff
        player.addEffect(WeightBarHelpers.getExhausted(false));

        return;
      }

      // if not overburdened or exhausted, reset flags
      WeightBarGUI.resetBooleanFlags();
    }
  }
}
