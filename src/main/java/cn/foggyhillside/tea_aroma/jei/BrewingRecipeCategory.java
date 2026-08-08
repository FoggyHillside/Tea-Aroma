package cn.foggyhillside.tea_aroma.jei;

import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.blocks.entities.states.KettleLiquid;
import cn.foggyhillside.tea_aroma.component.KettleContents;
import cn.foggyhillside.tea_aroma.recipe.BrewingRecipe;
import cn.foggyhillside.tea_aroma.registry.ModBlocks;
import cn.foggyhillside.tea_aroma.registry.ModDataComponents;
import cn.foggyhillside.tea_aroma.registry.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BrewingRecipeCategory implements IRecipeCategory<BrewingRecipe> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TeaAroma.MODID, "textures/gui/tea_aroma_gui_jei.png");

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
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public int getWidth() {
        return 122;
    }

    @Override
    public int getHeight() {
        return 52;
    }

    @Override
    public void draw(BrewingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics, 0, 0);
        this.teaAndArrow.draw(guiGraphics, 58, 8);
        this.itemStack.draw(guiGraphics, 59, 29);
        if (recipe.getLiquidType().equals("water") || recipe.getLiquidType().equals("boiling_water")) {
            this.water.draw(guiGraphics, 58, 23);
        } else if (recipe.getLiquidType().equals("milk") || recipe.getLiquidType().equals("boiling_milk")) {
            this.milk.draw(guiGraphics, 58, 23);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, BrewingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (58 <= mouseX && mouseX < 78 && 8 <= mouseY && mouseY < 27) {
            String type = recipe.getLiquidType();
            switch (type) {
                case "water" -> tooltip.add(Component.translatable("tea_aroma.tooltip.kettle.water"));
                case "boiling_water" -> tooltip.add(Component.translatable("tea_aroma.tooltip.kettle.boiling_water"));
                case "milk" -> tooltip.add(Component.translatable("tea_aroma.tooltip.kettle.milk"));
                case "boiling_milk" -> tooltip.add(Component.translatable("tea_aroma.tooltip.kettle.boiling_milk"));
            }
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BrewingRecipe recipe, IFocusGroup focuses) {
        ItemStack kettle = new ItemStack(ModItems.KETTLE.get());
        kettle.set(ModDataComponents.KETTLE_CONTENTS, new KettleContents(KettleLiquid.BOILING_WATER.toString(), 3, 0));
        builder.addSlot(RecipeIngredientRole.INPUT, 17, 17).addItemStacks(Arrays.asList((recipe.getIngredients().get(0)).getItems()));
        if (recipe.getIngredients().size() > 1) {
            builder.addSlot(RecipeIngredientRole.INPUT, 35, 17).addItemStacks(Arrays.asList((recipe.getIngredients().get(1)).getItems()));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 87, 17).addItemStack(recipe.getOutput());
        builder.addSlot(RecipeIngredientRole.CATALYST, 60, 30).addItemStack(kettle);
    }
}