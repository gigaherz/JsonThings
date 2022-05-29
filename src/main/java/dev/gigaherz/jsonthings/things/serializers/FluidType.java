package dev.gigaherz.jsonthings.things.serializers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.JsonThings;
import dev.gigaherz.jsonthings.things.IFlexFluid;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.builders.BlockBuilder;
import dev.gigaherz.jsonthings.things.builders.FluidBuilder;
import dev.gigaherz.jsonthings.things.fluids.FlexFlowingFluid;
import dev.gigaherz.jsonthings.things.fluids.FlexFluid;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.value.Any;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.util.Lazy;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("ClassCanBeRecord")
public class FluidType<T extends Fluid & IFlexFluid>
{
    public static final FluidType<FlexFluid> PLAIN = register("plain", (builder, data) -> new IFluidFactory<FlexFluid>()
    {
        @Override
        public FlexFluid construct(FluidBuilder builder)
        {
            List<Property<?>> _properties = builder.getProperties();
            Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
            return new FlexFluid(propertyDefaultValues)
            {
                @Override
                protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder1)
                {
                    super.createFluidStateDefinition(builder1);
                    _properties.forEach(builder1::add);
                }
            };
        }
    });

    public static final FluidType<FlexFlowingFluid> FLOWING = register("flowing", new IFluidSerializer<FlexFlowingFluid>()
    {
        private static void parseLiquidBlock(ResourceLocation name, Any val, Consumer<BlockBuilder> blockConsumer)
        {
            val
                    .ifBool(v -> v.handle(b -> {
                        if (b) createLiquidBlock(name, new JsonObject(), blockConsumer);
                    }))
                    .ifObj(obj -> obj.raw((JsonObject block) -> {
                        createLiquidBlock(name, block, blockConsumer);
                    }))
                    .typeError();
        }

        private static void createLiquidBlock(ResourceLocation name, JsonObject obj, Consumer<BlockBuilder> blockConsumer)
        {
            obj.addProperty("fluid", name.toString());
            blockConsumer.accept(JsonThings.blockParser.parseFromElement(name, obj, b -> {
                if (!b.hasBlockType())
                    b.setBlockType(BlockType.LIQUID);
            }));
        }

        @Override
        public IFluidFactory<FlexFlowingFluid> createFactory(ResourceLocation name, JsonObject data)
        {
            var slopeDistance = new MutableObject<>(4);
            var dropOff = new MutableObject<>(1);
            var canConvertToSource = new MutableObject<>(false);
            var tickDelay = new MutableObject<>(5);
            var explosionResistance = new MutableObject<>(100.0f);
            var block = new MutableObject<BlockBuilder>();

            JParse.begin(data)
                    .ifKey("slope_distance", any -> any.intValue().min(1).handle(slopeDistance::setValue))
                    .ifKey("dropoff", any -> any.intValue().range(1,8).handle(dropOff::setValue))
                    .ifKey("can_convert_to_source", any -> any.bool().handle(canConvertToSource::setValue))
                    .ifKey("tick_delay", any -> any.intValue().min(0).handle(tickDelay::setValue))
                    .ifKey("explosion_resistance", any -> any.floatValue().handle(explosionResistance::setValue))
                    .ifKey("block", any -> parseLiquidBlock(name, any, block::setValue));

            return new IFluidFactory<>()
            {
                @Override
                public FlexFlowingFluid construct(FluidBuilder builder)
                {
                    List<Property<?>> _properties = builder.getProperties();
                    Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
                    return new FlexFlowingFluid(_properties, propertyDefaultValues, slopeDistance.getValue(), dropOff.getValue(),
                            canConvertToSource.getValue(), tickDelay.getValue(), explosionResistance.getValue(), Lazy.of(() -> block.getValue().get().self()))
                    {
                        @Override
                        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder1)
                        {
                            super.createFluidStateDefinition(builder1);
                            _properties.forEach(builder1::add);
                        }
                    };
                }

                @Override
                public void register(FluidBuilder builder, BiConsumer<ResourceLocation, Fluid> register)
                {
                    register.accept(builder.getRegistryName(), builder.get().self());

                    var flowingName = new ResourceLocation(builder.getRegistryName().getNamespace(), builder.getRegistryName().getPath() + "_flowing");
                    register.accept(flowingName, ((FlowingFluid)builder.get().self()).getFlowing());
                }
            };
        }
    });

    public static void init()
    {
        /* do nothing */
    }

    public static <T extends Fluid & IFlexFluid> FluidType<T> register(String name, IFluidSerializer<T> factory, Property<?>... stockProperties)
    {
        return Registry.register(ThingRegistries.FLUID_TYPES, name, new FluidType<>(factory, Arrays.asList(stockProperties)));
    }

    private final IFluidSerializer<T> factory;
    private final List<Property<?>> stockProperties;

    private FluidType(IFluidSerializer<T> factory, List<Property<?>> stockProperties)
    {
        this.factory = factory;
        this.stockProperties = stockProperties;
    }

    public IFluidFactory<T> getFactory(ResourceLocation name, JsonObject data)
    {
        return factory.createFactory(name, data);
    }

    public List<Property<?>> getStockProperties()
    {
        return stockProperties;
    }

    public String toString()
    {
        return "FluidType{" + ThingRegistries.FLUID_TYPES.getKey(this) + "}";
    }
}
