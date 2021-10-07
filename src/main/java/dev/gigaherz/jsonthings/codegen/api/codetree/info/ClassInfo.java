package dev.gigaherz.jsonthings.codegen.api.codetree.info;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.type.TypeProxy;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public interface ClassInfo<T>
{
    TypeToken<? super T> superClass();
    TypeProxy<T> thisType();

    List<? extends MethodInfo<?>> constructors();
    List<? extends MethodInfo<?>> methods();
    List<? extends FieldInfo<?>> fields();

    ClassInfo<? super T> superClassInfo();

    default FieldInfo<?> getField(String fieldName)
    {
        return findField(fieldName).orElseThrow(() -> new IllegalStateException("No field found with name " + fieldName));
    }

    Optional<FieldInfo<?>> findField(String fieldName);
}
