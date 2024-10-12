package com.koenigstag.tfc_weight_bar;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import net.dries007.tfc.config.TFCConfig;

public final class ForgeEventHandler {

  public static void init() {
    final IEventBus bus = MinecraftForge.EVENT_BUS;

    bus.addListener(ForgeEventHandler::onPlayerTick);
  }

  // server side player tick event
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
      final int playerInvWeight = WeightBarHelpers.calculatePlayerFullWeight(player);

      // set current weight do display
      WeightBarData.serverChangeCurrentWeight(player, playerInvWeight);
    }
  }
}
