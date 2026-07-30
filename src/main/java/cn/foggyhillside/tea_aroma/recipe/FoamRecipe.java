package cn.foggyhillside.tea_aroma.recipe;

import cn.foggyhillside.tea_aroma.items.KettleItem;
import cn.foggyhillside.tea_aroma.registry.ModItems;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;

import javax.annotation.Nullable;

public class FoamRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;

    private final ItemStack output;

    private final ItemStack tea;

    public FoamRecipe(ResourceLocation id, ItemStack output, ItemStack tea) {
        this.id = id;
        this.output = output;
        this.tea = tea;
    }

    public ItemStack getTea() {
        return tea;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide) {
            return false;
        }
        boolean isBoilingMilkKettle = false;
        ItemStack kettle = pContainer.getItem(pContainer.getContainerSize() - 1);
        if (kettle.is(ModItems.KETTLE.get())) {
            if (KettleItem.getStackLiquid(kettle).equals("boiling_milk")) {
                isBoilingMilkKettle = true;
            }
        } else {
            isBoilingMilkKettle = true;
        }

        return isBoilingMilkKettle && pContainer.getItem(0).is(this.tea.getItem());
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess access) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return output;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return FoamRecipe.Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return FoamRecipe.Type.INSTANCE;
    }

    public static class Type implements RecipeType<FoamRecipe> {
        private Type() {
        }

        public static final FoamRecipe.Type INSTANCE = new FoamRecipe.Type();
    }

    public static class Serializer implements RecipeSerializer<FoamRecipe> {
        public static final FoamRecipe.Serializer INSTANCE = new FoamRecipe.Serializer();

        @Override
        public FoamRecipe fromJson(ResourceLocation location, JsonObject json) {
            ItemStack tea = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "tea"), true);
            if (tea.isEmpty()) {
                throw new JsonParseException("No ingredient for foam recipe");
            } else {
                ItemStack output = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
                return new FoamRecipe(location, output, tea);
            }
        }

        @Nullable
        public FoamRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            ItemStack output = buffer.readItem();
            ItemStack tea = buffer.readItem();
            return new FoamRecipe(id, output, tea);
        }

        public void toNetwork(FriendlyByteBuf buffer, FoamRecipe recipe) {
            buffer.writeItem(recipe.output);
            buffer.writeItem(recipe.tea);
        }
    }

}
