package dk.martinu.kofi.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE})
public @interface KofiOptions {
    boolean includeType() default true;
}
