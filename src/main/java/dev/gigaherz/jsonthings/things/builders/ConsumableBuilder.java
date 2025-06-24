package dev.gigaherz.jsonthings.things.builders;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.util.parse.JParseException;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Arrays;
import java.util.List;

public class ConsumableBuilder extends BaseBuilder<Consumable, ConsumableBuilder>
{
    public static ConsumableBuilder begin(ThingParser<Consumable, ConsumableBuilder> ownerParser, ResourceLocation registryName)
    {
        return new ConsumableBuilder(ownerParser, registryName);
    }

    private float consumeSeconds;
    private ItemUseAnimation animation;
    private ResourceLocation sound;
    private boolean hasConsumeParticles;
    private JsonElement[] onConsumeEffects;

    private ConsumableBuilder(ThingParser<Consumable, ConsumableBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Consummable";
    }

    public void setConsumeSeconds(float consumeSeconds)
    {
        this.consumeSeconds = consumeSeconds;
    }
    public void setAnimation(ItemUseAnimation animation)
    {
        this.animation = animation;
    }
    public void setSound(ResourceLocation sound)
    {
        this.sound = sound;
    }
    public void setHasConsumeParticles(boolean hasConsumeParticles)
    {
        this.hasConsumeParticles = hasConsumeParticles;
    }
    public void setOnConsumeEffects(JsonElement[] onConsumeEffects)
    {
        this.onConsumeEffects = onConsumeEffects;
    }


    @Override
    protected Consumable buildInternal()
    {
        soundEvent = DeferredHolder.create(Registries.SOUND_EVENT, sound);
        var consumeEffects = Arrays.stream(onConsumeEffects).map(e -> ConsumeEffect.CODEC.decode(JsonOps.INSTANCE, e).getOrThrow(JParseException::new).getFirst()).toList();
        return new Consumable(consumeSeconds, animation, soundEvent, hasConsumeParticles, consumeEffects);
    }

    private DeferredHolder<SoundEvent, SoundEvent> soundEvent;

    @Override
    public void validate()
    {
        if (soundEvent != null) soundEvent.value();
    }
}
