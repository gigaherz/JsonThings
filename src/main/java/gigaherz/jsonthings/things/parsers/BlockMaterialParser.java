package gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import gigaherz.jsonthings.things.ThingRegistries;
import gigaherz.jsonthings.things.builders.BlockMaterialBuilder;
import gigaherz.jsonthings.things.serializers.MaterialColors;
import gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;

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
                        .ifInteger(str -> builder.setColor(MaterialColor.MATERIAL_COLORS[str.range(0,64).getAsInt()]))
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
        return switch (s)
        {
            case "block" -> PushReaction.BLOCK;
            case "destroy" -> PushReaction.DESTROY;
            case "ignore" -> PushReaction.IGNORE;
            case "push_only" -> PushReaction.PUSH_ONLY;
            case "normal" -> PushReaction.NORMAL;
            default -> throw new IllegalStateException("'push_reaction' must be one of: \"block\", \"destroy\", \"ignore\", \"push_only\", \"normal\".");
        };
    }
}
