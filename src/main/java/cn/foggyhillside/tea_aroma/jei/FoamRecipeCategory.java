package cn.foggyhillside.tea_aroma.jei;

import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.recipe.FoamRecipe;
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
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FoamRecipeCategory implements IRecipeCategory<FoamRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(TeaAroma.MODID, "foam");

    public static final ResourceLocation TEXTURE = new ResourceLocation(TeaAroma.MODID,
            "textures/gui/tea_aroma_gui_jei.png");

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
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(FoamRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.teaAndArrow.draw(guiGraphics, 40, 8);
        this.itemStack.draw(guiGraphics, 41, 29);
        this.milk.draw(guiGraphics, 40, 23);

    }

    @Override
    public List<Component> getTooltipStrings(FoamRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (40 <= mouseX && mouseX < 60 && 8 <= mouseY && mouseY < 27) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("tea_aroma.tooltip.kettle.boiling_milk"));
            return tooltip;
        }

        return Collections.emptyList();

    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FoamRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 17, 17).addItemStack(recipe.getTea());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 69, 17).addItemStack(Utils.getResultItem(recipe));
        builder.addSlot(RecipeIngredientRole.CATALYST, 42, 30).addItemStack(new ItemStack(ModItems.KETTLE.get()));

    }
}
