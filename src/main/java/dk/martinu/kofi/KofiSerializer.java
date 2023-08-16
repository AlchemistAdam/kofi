package dk.martinu.kofi;

import org.jetbrains.annotations.NotNull;

public interface KofiSerializer {

    @NotNull
    KofiObject serialize(@NotNull Object obj);
}
