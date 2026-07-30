package cn.foggyhillside.tea_aroma.registry;

import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.recipe.BambooTrayRecipe;
import cn.foggyhillside.tea_aroma.recipe.FoamRecipe;
import cn.foggyhillside.tea_aroma.recipe.BrewingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TeaAroma.MODID);

    public static final RegistryObject<RecipeSerializer<BrewingRecipe>> BREWING_SERIALIZER =
            RECIPE_SERIALIZERS.register("brewing", () -> BrewingRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<FoamRecipe>> FOAM_SERIALIZER =
            RECIPE_SERIALIZERS.register("foam", () -> FoamRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<BambooTrayRecipe>> BAMBOO_TRAY_SERIALIZER =
            RECIPE_SERIALIZERS.register("bamboo_tray", () -> BambooTrayRecipe.Serializer.INSTANCE);
}
