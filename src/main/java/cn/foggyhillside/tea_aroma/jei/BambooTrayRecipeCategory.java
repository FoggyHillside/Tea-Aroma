package cn.foggyhillside.tea_aroma.jei;

import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.recipe.BambooTrayRecipe;
import cn.foggyhillside.tea_aroma.registry.ModBlocks;
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
public class BambooTrayRecipeCategory implements IRecipeCategory<BambooTrayRecipe> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TeaAroma.MODID, "textures/gui/tea_aroma_gui_jei.png");

    private final IDrawable background;

    private final IDrawable icon;

    private final IDrawable rolling;

    private final IDrawable fermentation;

    private final IDrawable withering;

    public BambooTrayRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 122, 52);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.BAMBOO_TRAY.get()));
        this.rolling = helper.createDrawable(TEXTURE, 196, 0, 20, 15);
        this.fermentation = helper.createDrawable(TEXTURE, 216, 0, 20, 15);
        this.withering = helper.createDrawable(TEXTURE, 236, 0, 20, 15);
    }

    @Override
    public RecipeType<BambooTrayRecipe> getRecipeType() {
        return JEIPlugin.BAMBOO_TRAY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("tea_aroma.jei.tea_processing");
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
    public void draw(BambooTrayRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics, 0, 0);
        if (recipe.getProcessType() == 1) {
            this.withering.draw(guiGraphics, 58, 12);
        } else if (recipe.getProcessType() == 2) {
            this.rolling.draw(guiGraphics, 58, 12);
        } else if (recipe.getProcessType() == 3) {
            this.fermentation.draw(guiGraphics, 58, 12);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, BambooTrayRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (58 <= mouseX && mouseX < 78 && 12 <= mouseY && mouseY < 27) {
            int type = recipe.getProcessType();
            switch (type) {
                case 1 -> tooltip.add(Component.translatable("tea_aroma.jei.withering"));
                case 2 -> tooltip.add(Component.translatable("tea_aroma.jei.rolling"));
                case 3 -> tooltip.add(Component.translatable("tea_aroma.jei.fermentation"));
            }
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BambooTrayRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 17, 17).addItemStacks(Arrays.asList((recipe.getIngredients().get(0)).getItems()));
        if (recipe.getIngredients().size() > 1) {
            builder.addSlot(RecipeIngredientRole.INPUT, 35, 17).addItemStacks(Arrays.asList((recipe.getIngredients().get(1)).getItems()));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 87, 17).addItemStack(recipe.getOutput());

    }
}