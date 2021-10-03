package dev.gigaherz.jsonthings.codegen.api;

import java.lang.annotation.Annotation;

public interface Annotatable<T>
{
    <A extends Annotation> T annotate(A a);
}
