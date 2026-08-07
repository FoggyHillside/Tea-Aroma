package cn.foggyhillside.tea_aroma.registry;

import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.recipe.BambooTrayRecipe;
import cn.foggyhillside.tea_aroma.recipe.FoamRecipe;
import cn.foggyhillside.tea_aroma.recipe.BrewingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, TeaAroma.MODID);

    public static final Supplier<RecipeSerializer<BrewingRecipe>> BREWING_SERIALIZER =
            RECIPE_SERIALIZERS.register("brewing", () -> BrewingRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeSerializer<FoamRecipe>> FOAM_SERIALIZER =
            RECIPE_SERIALIZERS.register("foam", () -> FoamRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeSerializer<BambooTrayRecipe>> BAMBOO_TRAY_SERIALIZER =
            RECIPE_SERIALIZERS.register("bamboo_tray", () -> BambooTrayRecipe.Serializer.INSTANCE);
}
