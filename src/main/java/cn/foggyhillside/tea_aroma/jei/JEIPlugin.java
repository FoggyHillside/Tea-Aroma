package cn.foggyhillside.tea_aroma.jei;

import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.recipe.BambooTrayRecipe;
import cn.foggyhillside.tea_aroma.recipe.BrewingRecipe;
import cn.foggyhillside.tea_aroma.recipe.FoamRecipe;
import cn.foggyhillside.tea_aroma.registry.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@JeiPlugin
public class JEIPlugin implements IModPlugin {
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModItems.BAMBOO_TRAY.get()), new RecipeType[]{BAMBOO_TRAY});
        registration.addRecipeCatalyst(new ItemStack(ModItems.CUP.get()), new RecipeType[]{BREW});
        registration.addRecipeCatalyst(new ItemStack(ModItems.KETTLE.get()), new RecipeType[]{FOAM});

    }

    public static RecipeType<BambooTrayRecipe> BAMBOO_TRAY =
            new RecipeType<>(BambooTrayRecipeCategory.UID, BambooTrayRecipe.class);

    public static RecipeType<BrewingRecipe> BREW =
            new RecipeType<>(BrewingRecipeCategory.UID, BrewingRecipe.class);

    public static RecipeType<FoamRecipe> FOAM =
            new RecipeType<>(FoamRecipeCategory.UID, FoamRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(TeaAroma.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new BambooTrayRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new BrewingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FoamRecipeCategory(registration.getJeiHelpers().getGuiHelper()));

    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<BambooTrayRecipe> stoveRecipes = recipeManager.getAllRecipesFor(BambooTrayRecipe.Type.INSTANCE);
        List<BrewingRecipe> brewingRecipes = recipeManager.getAllRecipesFor(BrewingRecipe.Type.INSTANCE);
        List<FoamRecipe> foamRecipes = recipeManager.getAllRecipesFor(FoamRecipe.Type.INSTANCE);

        registration.addRecipes(BAMBOO_TRAY, stoveRecipes);
        registration.addRecipes(BREW, brewingRecipes);
        registration.addRecipes(FOAM, foamRecipes);
    }
}
