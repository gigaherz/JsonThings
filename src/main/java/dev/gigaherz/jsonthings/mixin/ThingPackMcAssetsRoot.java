package dev.gigaherz.jsonthings.mixin;

import com.google.common.collect.ImmutableMap;
import dev.gigaherz.jsonthings.util.CustomPackType;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.net.URL;
import java.nio.file.Path;

@Mixin(VanillaPackResourcesBuilder.class)
public class ThingPackMcAssetsRoot
{
    // java/lang/Class.getResource (Ljava/lang/String;)Ljava/net/URL;
    @Inject(
            method="lambda$static$1()Lcom/google/common/collect/ImmutableMap;",
            at=@At(value="INVOKE", target="java/lang/Class.getResource (Ljava/lang/String;)Ljava/net/URL;")
    )
    public URL jsonthingsRedirectGetResource(
            String resourcePath,

            // LOCALS
            ImmutableMap.Builder<PackType, Path> builder,
            PackType packtype,
            String s
    )
    {
        return packtype == CustomPackType.THINGS ? CustomPackType.class.getResource(s) : VanillaPackResources.class.getResource(s);
    }

    /*
    @ModifyVariable(method="lambda$static$1()Lcom/google/common/collect/ImmutableMap;", name="url", at=@At("STORE"))
    private static URL jsonthingsModifyUrl(
            ImmutableMap.Builder<PackType, Path> builder,
            PackType packtype,
            String s,
            URL url,
            Exception exception,
            Path path,
            String s1,
            URI uri
    )
    {
        return packtype == CustomPackType.THINGS ? CustomPackType.class.getResource(s) : url;
    }
    */
}
