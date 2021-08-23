package gigaherz.jsonthings.things.serializers;

import com.google.gson.JsonObject;
import gigaherz.jsonthings.things.IFlexBlock;
import net.minecraft.world.level.block.Block;

public interface IBlockSerializer<T extends Block & IFlexBlock>
{
    IBlockFactory<T> createFactory(JsonObject data);
}
