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
import net.minecraft.world.item.crafting.RecipeHolder;
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
        registration.addRecipeCatalyst(new ItemStack(ModItems.BAMBOO_TRAY.get()), BAMBOO_TRAY);
        registration.addRecipeCatalyst(new ItemStack(ModItems.CUP.get()), BREW);
        registration.addRecipeCatalyst(new ItemStack(ModItems.KETTLE.get()), FOAM);

    }

    public static RecipeType<BambooTrayRecipe> BAMBOO_TRAY = RecipeType.create(TeaAroma.MODID, "bamboo_tray", BambooTrayRecipe.class);
    public static RecipeType<BrewingRecipe> BREW = RecipeType.create(TeaAroma.MODID, "brew", BrewingRecipe.class);
    public static RecipeType<FoamRecipe> FOAM = RecipeType.create(TeaAroma.MODID, "foam", FoamRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(TeaAroma.MODID, "jei_plugin");
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
        List<BambooTrayRecipe> trayRecipes = recipeManager.getAllRecipesFor(BambooTrayRecipe.Type.INSTANCE).stream()
                .map(RecipeHolder::value)
                .toList();
        List<BrewingRecipe> brewingRecipes = recipeManager.getAllRecipesFor(BrewingRecipe.Type.INSTANCE).stream()
                .map(RecipeHolder::value)
                .toList();
        List<FoamRecipe> foamRecipes = recipeManager.getAllRecipesFor(FoamRecipe.Type.INSTANCE).stream()
                .map(RecipeHolder::value)
                .toList();

        registration.addRecipes(BAMBOO_TRAY, trayRecipes);
        registration.addRecipes(BREW, brewingRecipes);
        registration.addRecipes(FOAM, foamRecipes);
    }
}