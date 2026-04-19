package dev.gigaherz.jsonthings.util;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.packs.OverlayMetadataSection;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;

public class CustomPackType
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final PackFormat PACK_FORMAT_VERSION = new PackFormat(11,0); // TODO: Bump when porting!
    public static final PackType THINGS = Enum.valueOf(PackType.class, "JSONTHINGS_THINGS");
    public static final MetadataSectionType<PackMetadataSection> THINGS_METADATA = new MetadataSectionType<>("pack", codecForPackType());
    public static final MetadataSectionType<PackMetadataSection> OPTIONAL_THINGS_METADATA = new MetadataSectionType<>("pack", metadataCodecForPackType());
    public static final MetadataSectionType<OverlayMetadataSection> THINGS_OVERLAY = new MetadataSectionType<>("jsonthings:overlays", OverlayMetadataSection.codecForPackType(PackType.SERVER_DATA));

    // Copied from mc PackMetadataSection
    private static Codec<PackMetadataSection> codecForPackType() {
        return RecordCodecBuilder.create(
                p_432485_ -> p_432485_.group(
                                ComponentSerialization.CODEC.fieldOf("description").forGetter(PackMetadataSection::description),
                                PackFormat.packCodec(THINGS).forGetter(PackMetadataSection::supportedFormats)
                        )
                        .apply(p_432485_, PackMetadataSection::new)
        );
    }

    // Copied from neoforge ResourcePackLoader
    private static final InclusiveRange<PackFormat> UNLIMITED_SUPPORT = new InclusiveRange<>(new PackFormat(0, 0), new PackFormat(Integer.MAX_VALUE, Integer.MAX_VALUE));
    private static Codec<PackMetadataSection> metadataCodecForPackType() {
        int lastPreMinor = 0;
        MapCodec<InclusiveRange<PackFormat>> formatCodec = PackFormat.IntermediaryFormat.PACK_CODEC.flatXmap(
                intermediary -> {
                    if (intermediary.min().isEmpty() && intermediary.max().isEmpty() && intermediary.format().isEmpty() && intermediary.supported().isEmpty()) {
                        return DataResult.success(UNLIMITED_SUPPORT);
                    }

                    var val = intermediary.validate(lastPreMinor, true, false, "Pack", "supported_formats");

                    if (val.isError() && parseContext != null)
                    {
                        var message = val.error().map(DataResult.Error::message).orElse("NO MESSAGE PROVIDED");
                        LOGGER.warn("IGNORING ERROR while parsing mod file as thingpack [{}] {} (mods no longer need a pack.mcmeta, the file can be deleted): {}", parseContext.id(), parseContext.title().getString(), message);
                        return DataResult.success(UNLIMITED_SUPPORT);
                    }

                    return val;
                },
                range -> {
                    if (range.equals(UNLIMITED_SUPPORT)) {
                        return DataResult.success(new PackFormat.IntermediaryFormat(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
                    }
                    return DataResult.success(PackFormat.IntermediaryFormat.fromRange(range, lastPreMinor));
                });
        return RecordCodecBuilder.create(
                in -> in.group(
                                ComponentSerialization.CODEC.optionalFieldOf("description", Component.empty()).forGetter(PackMetadataSection::description),
                                formatCodec.forGetter(PackMetadataSection::supportedFormats))
                        .apply(in, PackMetadataSection::new));
    }

    @Nullable
    private static PackLocationInfo parseContext = null;

    public static void internalParseContext(@Nullable PackLocationInfo location)
    {
        parseContext = location;
    }
}
