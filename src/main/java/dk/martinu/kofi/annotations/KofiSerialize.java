package dk.martinu.kofi.annotations;

import dk.martinu.kofi.KofiSerializer;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE})
public @interface KofiSerialize {
    Class<? extends KofiSerializer> with();
}
