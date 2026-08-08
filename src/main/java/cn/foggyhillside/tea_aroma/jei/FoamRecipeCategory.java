package cn.foggyhillside.tea_aroma.jei;

import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.blocks.entities.states.KettleLiquid;
import cn.foggyhillside.tea_aroma.component.KettleContents;
import cn.foggyhillside.tea_aroma.recipe.FoamRecipe;
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

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FoamRecipeCategory implements IRecipeCategory<FoamRecipe> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TeaAroma.MODID, "textures/gui/tea_aroma_gui_jei.png");

    private final IDrawable background;

    private final IDrawable icon;

    private final IDrawable teaAndArrow;

    private final IDrawable itemStack;

    private final IDrawable milk;

    public FoamRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 14, 54, 104, 52);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.KETTLE.get()));
        this.teaAndArrow = helper.createDrawable(TEXTURE, 236, 15, 20, 19);
        this.itemStack = helper.createDrawable(TEXTURE, 238, 34, 18, 18);
        this.milk = helper.createDrawable(TEXTURE, 236, 56, 20, 4);
    }

    @Override
    public RecipeType<FoamRecipe> getRecipeType() {
        return JEIPlugin.FOAM;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("tea_aroma.jei.foam");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public int getWidth() {
        return 104;
    }

    @Override
    public int getHeight() {
        return 52;
    }

    @Override
    public void draw(FoamRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics, 0, 0);
        this.teaAndArrow.draw(guiGraphics, 40, 8);
        this.itemStack.draw(guiGraphics, 41, 29);
        this.milk.draw(guiGraphics, 40, 23);

    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, FoamRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (40 <= mouseX && mouseX < 60 && 8 <= mouseY && mouseY < 27) {
            tooltip.add(Component.translatable("tea_aroma.tooltip.kettle.boiling_milk"));
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FoamRecipe recipe, IFocusGroup focuses) {
        ItemStack kettle = new ItemStack(ModItems.KETTLE.get());
        kettle.set(ModDataComponents.KETTLE_CONTENTS, new KettleContents(KettleLiquid.BOILING_MILK.toString(), 3, 0));
        builder.addSlot(RecipeIngredientRole.INPUT, 17, 17).addItemStack(recipe.getTea());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 69, 17).addItemStack(recipe.getOutput());
        builder.addSlot(RecipeIngredientRole.CATALYST, 42, 30).addItemStack(kettle);

    }
}