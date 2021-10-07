package dev.gigaherz.jsonthings.codegen.api;

import dev.gigaherz.jsonthings.codegen.api.codetree.info.ClassInfo;
import dev.gigaherz.jsonthings.codegen.type.TypeProxy;

public interface ClassDef<C> extends DefineClass<C>, ClassInfo<C>, TypeProxy<C>
{
}
