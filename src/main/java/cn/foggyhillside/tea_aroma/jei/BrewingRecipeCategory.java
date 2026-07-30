package cn.foggyhillside.tea_aroma.jei;

import cn.foggyhillside.tea_aroma.ModCompat;
import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.recipe.BrewingRecipe;
import cn.foggyhillside.tea_aroma.registry.ModBlocks;
import cn.foggyhillside.tea_aroma.registry.ModItems;
import cn.foggyhillside.tea_aroma.util.Utils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BrewingRecipeCategory implements IRecipeCategory<BrewingRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(TeaAroma.MODID, "brew");

    public static final ResourceLocation TEXTURE = new ResourceLocation(TeaAroma.MODID,
            "textures/gui/tea_aroma_gui_jei.png");

    private final IDrawable background;

    private final IDrawable icon;

    private final IDrawable teaAndArrow;

    private final IDrawable itemStack;

    private final IDrawable water;

    private final IDrawable milk;

    public BrewingRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 122, 52);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CUP.get()));
        this.teaAndArrow = helper.createDrawable(TEXTURE, 236, 15, 20, 19);
        this.itemStack = helper.createDrawable(TEXTURE, 238, 34, 18, 18);
        this.water = helper.createDrawable(TEXTURE, 236, 52, 20, 4);
        this.milk = helper.createDrawable(TEXTURE, 236, 56, 20, 4);
    }

    @Override
    public RecipeType<BrewingRecipe> getRecipeType() {
        return JEIPlugin.BREW;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("tea_aroma.jei.brew");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(BrewingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.teaAndArrow.draw(guiGraphics, 58, 8);
        this.itemStack.draw(guiGraphics, 59, 29);
        if (recipe.getLiquidType().equals("water") || recipe.getLiquidType().equals("boiling_water")) {
            this.water.draw(guiGraphics, 58, 23);
        } else if (recipe.getLiquidType().equals("milk") || recipe.getLiquidType().equals("boiling_milk")) {
            this.milk.draw(guiGraphics, 58, 23);
        }
    }

    @Override
    public List<Component> getTooltipStrings(BrewingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (58 <= mouseX && mouseX < 78 && 8 <= mouseY && mouseY < 27) {
            List<Component> tooltip = new ArrayList<>();
            String type = recipe.getLiquidType();
            if (type.equals("water")) {
                tooltip.add(Component.translatable("tea_aroma.tooltip.kettle.water"));
            } else if (type.equals("boiling_water")) {
                tooltip.add(Component.translatable("tea_aroma.tooltip.kettle.boiling_water"));
            } else if (type.equals("milk")) {
                tooltip.add(Component.translatable("tea_aroma.tooltip.kettle.milk"));
            } else if (type.equals("boiling_milk")) {
                tooltip.add(Component.translatable("tea_aroma.tooltip.kettle.boiling_milk"));
            }
            return tooltip;
        }
        return Collections.emptyList();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BrewingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 17, 17).addItemStacks(Arrays.asList((recipe.getIngredients().get(0)).getItems()));
        if (recipe.getIngredients().size() > 1) {
            builder.addSlot(RecipeIngredientRole.INPUT, 35, 17).addItemStacks(Arrays.asList((recipe.getIngredients().get(1)).getItems()));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 87, 17).addItemStack(Utils.getResultItem(recipe));
        if (ModCompat.isSimplyTeaLoaded() && recipe.getLiquidType().equals("ice")) {
            builder.addSlot(RecipeIngredientRole.INPUT, 60, 30).addItemStack(new ItemStack(ModCompat.getIceCube()));
        } else {
            builder.addSlot(RecipeIngredientRole.CATALYST, 60, 30).addItemStack(new ItemStack(ModItems.KETTLE.get()));
        }
    }
}
