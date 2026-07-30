package cn.foggyhillside.tea_aroma.registry;

import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.recipe.BambooTrayRecipe;
import cn.foggyhillside.tea_aroma.recipe.FoamRecipe;
import cn.foggyhillside.tea_aroma.recipe.BrewingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, TeaAroma.MODID);

    public static final RegistryObject<RecipeType<BrewingRecipe>> BREWING_RECIPE = RECIPE_TYPES.register("brewing", () -> registerRecipeType("brewing"));
    public static final RegistryObject<RecipeType<FoamRecipe>> FOAM_RECIPE = RECIPE_TYPES.register("foam", () -> registerRecipeType("foam"));
    public static final RegistryObject<RecipeType<BambooTrayRecipe>> BAMBOO_TRAY_RECIPE = RECIPE_TYPES.register("bamboo_tray", () -> registerRecipeType("bamboo_tray"));

    public static <T extends Recipe<?>> RecipeType<T> registerRecipeType(final String identifier) {
        return new RecipeType<>() {
            public String toString() {
                return TeaAroma.MODID + ":" + identifier;
            }
        };
    }
}
