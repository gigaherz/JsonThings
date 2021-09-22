package gigaherz.jsonthings.util.parse;

import java.util.function.Consumer;

public interface ObjValue
{
    ObjValue key(String keyName, Consumer<Any> visitor);

    ObjValue ifKey(String keyName, Consumer<Any> visitor);
}
