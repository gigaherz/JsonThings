package dev.gigaherz.jsonthings.mixin;

import dev.gigaherz.jsonthings.things.parsers.ThingResourceManager;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.server.Main.class)
public class ServerMainMixin
{
    @Redirect(method = "main([Ljava/lang/String;)V",
            at = @At(value = "NEW", target = "(Lnet/minecraft/server/packs/PackType;[Lnet/minecraft/server/packs/repository/RepositorySource;)Lnet/minecraft/server/packs/repository/PackRepository;")
    )
    private static PackRepository redirectPackListCreation(PackType type, RepositorySource... finders)
    {
        PackRepository list = new PackRepository(type, finders);
        list.addPackFinder(ThingResourceManager.instance().getWrappedPackFinder());
        return list;
    }
}
