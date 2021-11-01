package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.builders.BlockMaterialBuilder;
import dev.gigaherz.jsonthings.things.serializers.MaterialColors;
import dev.gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class BlockMaterialParser extends ThingParser<BlockMaterialBuilder>
{
    public BlockMaterialParser()
    {
        super(GSON, "block_material");
    }

    @Override
    public void finishLoading()
    {
        getBuilders().forEach(thing -> Registry.register(ThingRegistries.BLOCK_MATERIALS, thing.getRegistryName(), thing.get()));
    }

    @Override
    public BlockMaterialBuilder processThing(ResourceLocation key, JsonObject data)
    {
        final BlockMaterialBuilder builder = BlockMaterialBuilder.begin(key);

        JParse.begin(data)
                .obj()
                .key("map_color", val -> val
                        .ifString(str -> builder.setColor(MaterialColors.get(str.getAsString())))
                        .ifInteger(str -> builder.setColor(MaterialColor.MATERIAL_COLORS[str.range(0, 64).getAsInt()]))
                        .typeError()
                )
                .ifKey("liquid", val -> val.bool().handle(builder::setLiquid))
                .ifKey("flammable", val -> val.bool().handle(builder::setFlammable))
                .ifKey("replaceable", val -> val.bool().handle(builder::setReplaceable))
                .ifKey("solid", val -> val.bool().handle(builder::setSolid))
                .ifKey("blocks_motion", val -> val.bool().handle(builder::setBlocksMotion))
                .ifKey("solid_blocking", val -> val.bool().handle(builder::setSolidBlocking))
                .ifKey("push_reaction", val -> val.string().map(BlockMaterialParser::parsePushReaction).handle(builder::setPushReaction));

        return builder;
    }

    private static PushReaction parsePushReaction(String s)
    {
        switch (s)
        {
            case "block":
                return PushReaction.BLOCK;
            case "destroy":
                return PushReaction.DESTROY;
            case "ignore":
                return PushReaction.IGNORE;
            case "push_only":
                return PushReaction.PUSH_ONLY;
            case "normal":
                return PushReaction.NORMAL;
            default:
                throw new IllegalStateException("'push_reaction' must be one of: \"block\", \"destroy\", \"ignore\", \"push_only\", \"normal\".");
        }
    }
}
