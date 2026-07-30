package cn.foggyhillside.tea_aroma.recipe;

import cn.foggyhillside.tea_aroma.ModCompat;
import cn.foggyhillside.tea_aroma.items.KettleItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.RecipeMatcher;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BrewingRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;

    private final ItemStack output;

    private final NonNullList<Ingredient> ingredients;

    private final String liquidType;

    public BrewingRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> ingredients, String liquidType) {
        this.id = id;
        this.output = output;
        this.ingredients = ingredients;
        this.liquidType = liquidType;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public String getLiquidType() {
        return liquidType;
    }

    private void loadList(List<ItemStack> list, int id) {
        list.clear();
        for (int i = 0; i < ingredients.get(id).getItems().length; i++) {
            ItemStack itemstack = ingredients.get(id).getItems()[i];
            if (!itemstack.isEmpty()) {
                list.add(itemstack);
            }
        }
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide) {
            return false;
        }
        ItemStack stack = pContainer.getItem(pContainer.getContainerSize() - 1);
        List<ItemStack> inputs = new ArrayList<>(0);
        boolean isLiquidMatched = false;

        for (int i = 0; i < pContainer.getContainerSize() - 1; i++) {
            ItemStack itemstack = pContainer.getItem(i);
            if (!itemstack.isEmpty()) {
                inputs.add(itemstack);
            }
        }
        if (stack.getItem() instanceof KettleItem) {
            if (KettleItem.getStackLiquid(stack).equals(this.liquidType)) {
                isLiquidMatched = true;
            }
        } else if (ModCompat.isSimplyTeaLoaded()) {
            if (this.liquidType.equals("ice") && stack.is(ModCompat.getIceCube())) {
                isLiquidMatched = true;
            } else if (stack.is(ModCompat.getTeapotHot()) && this.liquidType.equals("boiling_water")) {
                isLiquidMatched = true;
            } else if (stack.is(ModCompat.getTeapotFrothed()) && this.liquidType.equals("boiling_milk")) {
                isLiquidMatched = true;
            }
        }
        return RecipeMatcher.findMatches(inputs, ingredients) != null && isLiquidMatched;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
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
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<BrewingRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<BrewingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        private static int getLiquidTypeInt(String type) {
            return switch (type) {
                case "milk" -> 2;
                case "boiling_water" -> 3;
                case "boiling_milk" -> 4;
                case "ice" -> 5;
                default -> 1;
            };
        }

        private static String getLiquidType(int type) {
            return switch (type) {
                case 2 -> "milk";
                case 3 -> "boiling_water";
                case 4 -> "boiling_milk";
                case 5 -> "ice";
                default -> "water";
            };
        }

        @Override
        public BrewingRecipe fromJson(ResourceLocation location, JsonObject json) {
            String kettleType = GsonHelper.getAsString(json, "liquid_type", "boiling_water");
            NonNullList<Ingredient> ingredients = readIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (getLiquidTypeInt(kettleType) < 1 || getLiquidTypeInt(kettleType) > 5) {
                throw new JsonParseException("Invalid kettle type");
            } else if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for brewing recipe");
            } else if (ingredients.size() > 2) {
                throw new JsonParseException("Too many ingredients for brewing recipe! The max is 2");
            } else {
                ItemStack output = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
                return new BrewingRecipe(location, output, ingredients, kettleType);
            }
        }

        private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for (int i = 0; i < ingredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
                if (!ingredient.isEmpty()) {
                    nonnulllist.add(ingredient);
                }
            }
            return nonnulllist;
        }

        @Nullable
        public BrewingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            NonNullList<Ingredient> inputItemsIn = NonNullList.withSize(i, Ingredient.EMPTY);

            inputItemsIn.replaceAll(ignored -> Ingredient.fromNetwork(buffer));

            ItemStack output = buffer.readItem();
            int kettleTypeInt = buffer.readVarInt();
            return new BrewingRecipe(id, output, inputItemsIn, BrewingRecipe.Serializer.getLiquidType(kettleTypeInt));
        }

        public void toNetwork(FriendlyByteBuf buffer, BrewingRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());

            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.output);
            buffer.writeVarInt(BrewingRecipe.Serializer.getLiquidTypeInt(recipe.liquidType));
        }
    }

}
