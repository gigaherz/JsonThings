package dev.gigaherz.jsonthings.things.serializers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.IFlexBlock;
import net.minecraft.block.Block;

public interface IBlockSerializer<T extends Block & IFlexBlock>
{
    IBlockFactory<T> createFactory(JsonObject data);
}
