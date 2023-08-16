package dk.martinu.kofi;

import org.jetbrains.annotations.NotNull;

public interface KofiDeserializer {

    @NotNull
    Object deserialize(@NotNull KofiObject obj);
}
