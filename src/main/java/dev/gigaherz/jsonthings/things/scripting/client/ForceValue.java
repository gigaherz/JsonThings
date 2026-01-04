package dev.gigaherz.jsonthings.things.scripting.client;

import static dev.gigaherz.jsonthings.things.scripting.McFunctionScript.*;

import com.google.gson.JsonObject;

import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventType;
import dev.gigaherz.jsonthings.util.parse.JParse;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

@OnlyIn(Dist.CLIENT)
public class ForceValue implements IClientLogic {
    public int value;

    public ForceValue(JsonObject data) {
        JParse.begin(data).key("value", val -> this.value = val.intValue().getAsInt());
    }

    @Override
    public Object getResult(FlexEventType<?> event, FlexEventContext context) {
        return getResultByEventType(event, context, value);
    }
}
