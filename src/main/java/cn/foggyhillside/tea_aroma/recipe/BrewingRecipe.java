package cn.foggyhillside.tea_aroma.recipe;

import cn.foggyhillside.tea_aroma.blocks.entities.states.KettleLiquid;
import cn.foggyhillside.tea_aroma.items.KettleItem;
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
import java.util.Arrays;
import java.util.List;

public class BrewingRecipe implements Recipe<RecipeInput> {

    private final ItemStack output;

    private final NonNullList<Ingredient> ingredients;

    private final String liquidType;

    public BrewingRecipe(ItemStack output, NonNullList<Ingredient> ingredients, String liquidType) {
        this.output = output;
        this.ingredients = ingredients;
        this.liquidType = liquidType;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ItemStack getOutput() {
        return output;
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
    public boolean matches(RecipeInput pInput, Level pLevel) {
        if (pLevel.isClientSide) {
            return false;
        }

        ItemStack stack = pInput.getItem(pInput.size() - 1);
        List<ItemStack> inputs = new ArrayList<>(0);
        boolean isLiquidMatched = KettleItem.getStackLiquid(stack).equals(this.liquidType)
                && stack.getItem() instanceof KettleItem;

        for (int i = 0; i < pInput.size() - 1; i++) {
            ItemStack itemstack = pInput.getItem(i);
            if (!itemstack.isEmpty()) {
                inputs.add(itemstack);
            }
        }

        return RecipeMatcher.findMatches(inputs, ingredients) != null && isLiquidMatched;
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

    public static class Type implements RecipeType<BrewingRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<BrewingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        public static final MapCodec<BrewingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.optionalFieldOf("liquid_type", "boiling_water")
                                .forGetter(r -> r.liquidType),
                        Ingredient.CODEC.listOf().fieldOf("ingredients")
                                .forGetter(r -> r.ingredients),
                        ItemStack.OPTIONAL_CODEC.fieldOf("result")
                                .forGetter(r -> r.output)
                ).apply(instance, (liquidType, ingredients, output) -> {
                    boolean valid = Arrays.stream(KettleLiquid.values())
                            .filter(e -> e != KettleLiquid.NONE)
                            .anyMatch(e -> e.getSerializedName().equals(liquidType));
                    if (!valid)
                        throw new IllegalArgumentException("Invalid kettle type: " + liquidType);
                    if (ingredients.isEmpty())
                        throw new IllegalArgumentException("No ingredients for brewing recipe");
                    if (ingredients.size() > 2)
                        throw new IllegalArgumentException("Too many ingredients! The maximum is 2");
                    NonNullList<Ingredient> list = NonNullList.create();
                    list.addAll(ingredients);
                    return new BrewingRecipe(output, list, liquidType);
                })
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, BrewingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8, r -> r.liquidType,
                        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.ingredients,
                        ItemStack.OPTIONAL_STREAM_CODEC, r -> r.output,
                        (liquidType, ingredients, output) -> {
                            NonNullList<Ingredient> list = NonNullList.create();
                            list.addAll(ingredients);
                            return new BrewingRecipe(output, list, liquidType);
                        }
                );

        @Override
        public MapCodec<BrewingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BrewingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
