package gigaherz.jsonthings.misc;

import java.util.function.Function;

public class AlternativeTest
{
    private static void test()
    {
        Either<Integer, RuntimeException> main = Either.ofMain(2);
        Either<Integer, RuntimeException> alt = Either.ofOther(new RuntimeException());

        main.ifOther(System.out::println).orElseThrow(Function.identity());
        alt.toOptional().ifPresent(System.out::println);
    }
}
