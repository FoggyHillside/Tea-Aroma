package cn.foggyhillside.tea_aroma.recipe;

import cn.foggyhillside.tea_aroma.blocks.entities.states.KettleLiquid;
import cn.foggyhillside.tea_aroma.items.KettleItem;
import cn.foggyhillside.tea_aroma.registry.ModItems;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class FoamRecipe implements Recipe<RecipeInput> {
    private final ItemStack output;
    private final ItemStack tea;

    public FoamRecipe(ItemStack output, ItemStack tea) {
        this.output = output;
        this.tea = tea;
    }

    public ItemStack getTea() {
        return tea;
    }

    public ItemStack getOutput() {
        return output;
    }

    @Override
    public boolean matches(RecipeInput pInput, Level pLevel) {
        if (pLevel.isClientSide) {
            return false;
        }

        ItemStack stack = pInput.getItem(pInput.size() - 1);
        boolean isBoilingMilkKettle = stack.is(ModItems.KETTLE.get())
                && KettleItem.getStackLiquid(stack).equals(KettleLiquid.BOILING_MILK.toString());

        return isBoilingMilkKettle && pInput.getItem(0).is(this.tea.getItem());
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

    public static class Type implements RecipeType<FoamRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<FoamRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        public static final MapCodec<FoamRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ItemStack.OPTIONAL_CODEC.fieldOf("tea").forGetter(r -> r.tea),
                        ItemStack.OPTIONAL_CODEC.fieldOf("result").forGetter(r -> r.output)
                ).apply(instance, (tea, output) -> {
                    if (tea.isEmpty())
                        throw new IllegalArgumentException("No ingredient for foam recipe");
                    return new FoamRecipe(output, tea);
                })
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, FoamRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ItemStack.OPTIONAL_STREAM_CODEC, r -> r.output,
                        ItemStack.OPTIONAL_STREAM_CODEC, r -> r.tea,
                        FoamRecipe::new
                );

        @Override
        public MapCodec<FoamRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FoamRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
