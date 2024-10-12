package com.koenigstag.tfc_weight_bar;

import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandlerModifiable;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import net.dries007.tfc.common.capabilities.size.IItemSize;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.common.capabilities.size.Weight;
import net.dries007.tfc.common.TFCEffects;
import net.dries007.tfc.util.Helpers;

public class WeightBarHelpers {

  public static int getMaxInventoryWeight(Player player) {
    Container container = player.getInventory();

    if (container == null) {
      return 0;
    }

    return Config.maxInvWeight;
  }

  public static double getBarPercentage(int currentWeight, Player player) {
    // 80 / 100 = 0.8
    return currentWeight / getMaxInventoryWeight(player);
  }

  public static int getItemStackWeightInt(ItemStack itemStack) {
    if (!itemStack.isEmpty()) {
      IItemSize size = ItemSizeManager.get(itemStack);

      int weightInt = Config.getWeightIntConfig(size.getWeight(itemStack));
      int sizeInt = Config.getSizeIntConfig(size.getSize(itemStack));

      return weightInt + sizeInt;
    }

    return 0;
  }

  public static int calculateContainerWeight(final Container container) {
    if (container == null) {
      return 0;
    }

    int totalWeight = 0;

    for (int i = 0; i < container.getContainerSize(); i++) {
      ItemStack itemStack = container.getItem(i);

      totalWeight += getItemStackWeightInt(itemStack);
    }

    return totalWeight;
  }

  public static int calculateCuriosSlotsWeight(final Player livingEntity) {
    int curiosWeightInt = 0;

    // Counts Curios slots to the full weight
    if (ModList.get().isLoaded("curios")) {
      LazyOptional<ICuriosItemHandler> lazyCuriosInventory = CuriosApi.getCuriosInventory(livingEntity);

      if (lazyCuriosInventory.isPresent()) {
        ICuriosItemHandler curiosInventory = lazyCuriosInventory.resolve().get();
        IItemHandlerModifiable curiosContainer = curiosInventory.getEquippedCurios();

        for (int i = 0; i < curiosContainer.getSlots(); i++) {
          final ItemStack stack = curiosContainer.getStackInSlot(i);

          curiosWeightInt += WeightBarHelpers.getItemStackWeightInt(stack);
        }
      }
    }

    return curiosWeightInt;
  }

  public static int calculatePlayerFullWeight(final Player livingEntity) {
    int totalWeight = 0;

    totalWeight += calculateContainerWeight(livingEntity.getInventory());
    totalWeight += calculateCuriosSlotsWeight(livingEntity);

    return totalWeight;
  }

  public static boolean isPlayerExhausted(int playerInvWeight, int maxInvWeight) {
    return playerInvWeight >= maxInvWeight * Config.getExhaustedWeightCoefficient();
  }

  public static boolean isPlayerOverburdened(int playerInvWeight, int maxInvWeight) {
    return playerInvWeight > maxInvWeight;
  }

  public static MobEffectInstance getOverburdened(boolean visible) {
    return new MobEffectInstance(TFCEffects.OVERBURDENED.holder(), Config.calculateWeightEachNTicks + 5, 0, false,
        visible);
  }

  public static MobEffectInstance getExhausted(boolean visible) {
    return new MobEffectInstance(TFCEffects.EXHAUSTED.holder(), Config.calculateWeightEachNTicks + 5, 0, false,
        visible);
  }
}
