package cn.foggyhillside.tea_aroma;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class FoodList {
    public static FoodProperties.Builder tea() {
        return new FoodProperties.Builder().nutrition(1).saturationModifier(0.25F).alwaysEdible();
    }

    public static FoodProperties.Builder teaLatte() {
        return new FoodProperties.Builder().nutrition(2).saturationModifier(0.25F).alwaysEdible();
    }

    //Tea
    public static final FoodProperties BAMBOO_TEA = tea().effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 25 * 20, 0), 1.0F).build();
    public static final FoodProperties WHITE_TEA = tea().effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 5 * 20, 0), 1.0F).build();
    public static final FoodProperties GREEN_TEA = tea().effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 10 * 20, 0), 1.0F).build();
    public static final FoodProperties BLACK_TEA = tea().effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 50 * 20, 0), 1.0F).build();
    public static final FoodProperties YELLOW_TEA = tea().effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 30 * 20, 0), 1.0F).build();
    public static final FoodProperties OOLONG_TEA = tea().effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30 * 20, 0), 1.0F).build();
    public static final FoodProperties DARK_TEA = tea().effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 50 * 20, 0), 1.0F).build();
    public static final FoodProperties ROSE_TEA = tea().effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 10 * 20, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 10 * 20, 0), 1.0F).build();
    public static final FoodProperties DANDELION_TEA = tea().effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 10 * 20, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.SATURATION, 7, 0), 1.0F).build();
    public static final FoodProperties LILAC_TEA = tea().effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 10 * 20, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 10 * 20, 0), 1.0F).build();
    public static final FoodProperties BLUE_ORCHID_TEA = tea().effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 10 * 20, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.SATURATION, 7, 0), 1.0F).build();
    //Tea Latte
    public static final FoodProperties BAMBOO_TEA_LATTE = teaLatte().effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 25 * 20, 0), 1.0F).build();
    public static final FoodProperties WHITE_TEA_LATTE = teaLatte().effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 5 * 20, 0), 1.0F).build();
    public static final FoodProperties GREEN_TEA_LATTE = teaLatte().effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 10 * 20, 0), 1.0F).build();
    public static final FoodProperties BLACK_TEA_LATTE = teaLatte().effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 50 * 20, 0), 1.0F).build();
    public static final FoodProperties YELLOW_TEA_LATTE = teaLatte().effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 30 * 20, 0), 1.0F).build();
    public static final FoodProperties OOLONG_TEA_LATTE = teaLatte().effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30 * 20, 0), 1.0F).build();
    public static final FoodProperties DARK_TEA_LATTE = teaLatte().effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 50 * 20, 0), 1.0F).build();
    public static final FoodProperties ROSE_TEA_LATTE = teaLatte().effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 10 * 20, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 10 * 20, 0), 1.0F).build();
    public static final FoodProperties DANDELION_TEA_LATTE = teaLatte().effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 10 * 20, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.SATURATION, 7, 0), 1.0F).build();
    public static final FoodProperties LILAC_TEA_LATTE = teaLatte().effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 10 * 20, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 10 * 20, 0), 1.0F).build();
    public static final FoodProperties BLUE_ORCHID_TEA_LATTE = teaLatte().effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 10 * 20, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.SATURATION, 7, 0), 1.0F).build();
}