package gigaherz.jsonthings.codegen.type;

import com.google.common.reflect.TypeToken;

@SuppressWarnings("UnstableApiUsage")
public class TypeTokenProxy<T> extends TypeToken<T> implements TypeTokenSupplier<T>
{
    private TypeToken<T> type;

    @Override
    public TypeToken<T> actualType()
    {
        if (type == null)
            throw new IllegalStateException("Type has not been calculated yet.");
        return this.type;
    }

    public void setActualType(TypeToken<T> type)
    {
        this.type = type;
    }
}
