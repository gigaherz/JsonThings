package gigaherz.jsonthings.item.builder;

import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class ModelInfo
{
    public class ModelMapping
    {
        public final int metadata; // to be removed in 1.13
        public final ResourceLocation fileName;
        public final String variantName;

        public ModelMapping(int metadata, String fileName, String variantName)
        {
            this.metadata = metadata;
            this.fileName = new ResourceLocation(fileName);
            this.variantName = variantName;
        }
    }

    public final List<ModelMapping> mappings = Lists.newArrayList();

    public void addMapping(int metadata, String fileName, String variantName)
    {
        mappings.add(new ModelMapping(metadata, fileName, variantName));
    }
}
