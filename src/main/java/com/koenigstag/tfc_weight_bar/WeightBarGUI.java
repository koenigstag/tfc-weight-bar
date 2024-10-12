package com.koenigstag.tfc_weight_bar;

public class WeightBarGUI {
  public static int currentWeight;
  public static boolean isExhausted;
  public static boolean isOverburdened;

  public static int getCurrentWeight() {
    return WeightBarGUI.currentWeight;
  }

  public static void changeCurrentWeight(int newWeight) {
    WeightBarGUI.currentWeight = newWeight;

    triggerSideEffects();
  }

  public static void setIsExhausted(boolean isExhausted) {
    WeightBarGUI.isExhausted = isExhausted;
    WeightBarGUI.isOverburdened = false;

    triggerSideEffects();
  }

  public static void setIsOverburdened(boolean isOverburdened) {
    WeightBarGUI.isOverburdened = isOverburdened;
    WeightBarGUI.isExhausted = false;

    triggerSideEffects();
  }

  public static void resetBooleanFlags() {
    WeightBarGUI.isExhausted = false;
    WeightBarGUI.isOverburdened = false;
  }

  public static void triggerSideEffects() {
    // TODO side effects
  }

  public static String getBarColor() {
    if (WeightBarGUI.isOverburdened) {
      return Config.getOverburdenedBarColor();
    } else if (WeightBarGUI.isExhausted) {
      return Config.getExhaustedBarColor();
    } else {
      return Config.getNormalBarColor();
    }
  }
}
