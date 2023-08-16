package dk.martinu.kofi.annotations;

import dk.martinu.kofi.KofiDeserializer;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE})
public @interface KofiDeserialize {
    Class<? extends KofiDeserializer> with();
}
