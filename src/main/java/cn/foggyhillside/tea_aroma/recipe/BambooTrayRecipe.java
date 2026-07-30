package cn.foggyhillside.tea_aroma.recipe;

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

public class BambooTrayRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;

    private final ItemStack output;

    private final NonNullList<Ingredient> ingredients;

    private final int processType;

    public BambooTrayRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> ingredients, int processType) {
        this.id = id;
        this.output = output;
        this.ingredients = ingredients;
        this.processType = processType;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public int getProcessType() {
        return processType;
    }

    @Override
    public boolean matches(SimpleContainer inventory, Level level) {
        if (level.isClientSide) {
            return false;
        }
        List<ItemStack> inputs = new ArrayList<>();

        for (int j = 0; j < 2; j++) {
            ItemStack itemstack = inventory.getItem(j);
            if (!itemstack.isEmpty()) {
                inputs.add(itemstack);
            }
        }
        return RecipeMatcher.findMatches(inputs, this.ingredients) != null;
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
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<BambooTrayRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<BambooTrayRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public BambooTrayRecipe fromJson(ResourceLocation location, JsonObject json) {
            int processType = GsonHelper.getAsInt(json, "process_type", 1);
            NonNullList<Ingredient> ingredients = readIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (processType < 1 || processType > 3) {
                throw new JsonParseException("Invalid operation type");
            } else if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for bamboo tray recipe");
            } else if (ingredients.size() > 2) {
                throw new JsonParseException("Too many ingredients for bamboo tray recipe! The max is 2");
            } else {
                ItemStack output = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
                return new BambooTrayRecipe(location, output , ingredients, processType);
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
        public BambooTrayRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            NonNullList<Ingredient> inputItemsIn = NonNullList.withSize(i, Ingredient.EMPTY);

            inputItemsIn.replaceAll(ignored -> Ingredient.fromNetwork(buffer));

            ItemStack output = buffer.readItem();
            int type = buffer.readVarInt();
            return new BambooTrayRecipe(id, output , inputItemsIn, type);
        }

        public void toNetwork(FriendlyByteBuf buffer, BambooTrayRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());

            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.output);
            buffer.writeVarInt(recipe.processType);
        }
    }

}
