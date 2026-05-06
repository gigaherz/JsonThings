package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.things.scripting.McFunctionScript;
import dev.gigaherz.jsonthings.things.scripting.client.IClientLogic;
import net.minecraft.resources.ResourceLocation;

public class McFunctionScriptBuilder extends BaseBuilder<McFunctionScript, McFunctionScriptBuilder> {
    public McFunctionScriptBuilder(ThingParser<McFunctionScriptBuilder> ownerParser, ResourceLocation registryName) {
        super(ownerParser, registryName);
    }

    public String function;
    public Boolean debug = false;
    public String item;
    public String block;
    public String hand;
    public IClientLogic clientLogic = IClientLogic.DEFAULT;
    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    @Override
    protected String getThingTypeDisplayName() {
        return "Minecraft Function Script";
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public void setClientLogic(IClientLogic clientLogic) {
        this.clientLogic = clientLogic;
    }

    @Override
    protected McFunctionScript buildInternal() {
        return new McFunctionScript(function, debug, clientLogic);
    }
}
