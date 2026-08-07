package cn.foggyhillside.tea_aroma.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;

import java.util.ArrayList;
import java.util.List;

public class BambooTrayRecipe implements Recipe<RecipeInput> {
    private final ItemStack output;
    private final NonNullList<Ingredient> ingredients;
    private final int processType;

    public BambooTrayRecipe(ItemStack output, NonNullList<Ingredient> ingredients, int processType) {
        this.output = output;
        this.ingredients = ingredients;
        this.processType = processType;
    }

    public int getProcessType() {
        return processType;
    }

    public ItemStack getOutput() {
        return output;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean matches(RecipeInput pInput, Level pLevel) {
        if (pLevel.isClientSide) {
            return false;
        }
        List<ItemStack> inputs = new ArrayList<>();

        for (int j = 0; j < 2; j++) {
            ItemStack itemstack = pInput.getItem(j);
            if (!itemstack.isEmpty()) {
                inputs.add(itemstack);
            }
        }
        return RecipeMatcher.findMatches(inputs, this.ingredients) != null;
    }

    @Override
    public ItemStack assemble(RecipeInput pInput, HolderLookup.Provider pRegistries) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return output;
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
        public static final BambooTrayRecipe.Type INSTANCE = new BambooTrayRecipe.Type();
    }

    public static class Serializer implements RecipeSerializer<BambooTrayRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<BambooTrayRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.INT.optionalFieldOf("process_type", 1).forGetter(r -> r.processType),
                        Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(r -> r.ingredients),
                        ItemStack.OPTIONAL_CODEC.fieldOf("result").forGetter(r -> r.output)
                ).apply(instance, (processType, ingredients, output) -> {
                    if (processType < 1 || processType > 3)
                        throw new IllegalArgumentException("Invalid operation type: " + processType);
                    if (ingredients.isEmpty())
                        throw new IllegalArgumentException("No ingredients for bamboo tray recipe");
                    if (ingredients.size() > 2)
                        throw new IllegalArgumentException("Too many ingredients! The maximum is 2");
                    NonNullList<Ingredient> list = NonNullList.create();
                    list.addAll(ingredients);
                    return new BambooTrayRecipe(output, list, processType);
                })
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, BambooTrayRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.VAR_INT, r -> r.processType,
                        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.ingredients,
                        ItemStack.OPTIONAL_STREAM_CODEC, r -> r.output,
                        (processType, ingredients, output) -> {
                            NonNullList<Ingredient> list = NonNullList.create();
                            list.addAll(ingredients);
                            return new BambooTrayRecipe(output, list, processType);
                        }
                );

        @Override
        public MapCodec<BambooTrayRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BambooTrayRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
