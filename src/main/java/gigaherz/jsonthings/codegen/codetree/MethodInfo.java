package gigaherz.jsonthings.codegen.codetree;

import com.google.common.collect.Lists;

import java.lang.reflect.Modifier;
import java.util.List;

public class MethodInfo
{
    public List<ParamInfo> params = Lists.newArrayList();
    public ClassInfo<?> owner;
    public String name;
    public int modifiers;

    public boolean isStatic()
    {
        return Modifier.isStatic(modifiers);
    }
}
