package com.search.suggestion.common;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;

/**
 * Condition that must be true prior to a routine's execution.
 */
public final class Precondition
{
    @SuppressWarnings("checkstyle:leftcurly")
    private Precondition() { };

    /**
     * Ensures the truth of an expression.
     *
     * @throws NoSuchElementException if {@code expression} is false;
     */
    public static void checkElement(boolean expression)
    {
        if (!expression)
        {
            throw new NoSuchElementException();
        }
    }

    /**
     * Ensures the truth of an expression.
     *
     * @throws NoSuchElementException if {@code expression} is false;
     */
    public static void checkElement(boolean expression, @Nullable String message)
    {
        if (!expression)
        {
            throw new NoSuchElementException(message);
        }
    }

    /**
     * Ensures the truth of an expression.
     *
     * @throws NullPointerException if {@code expression} is false;
     */
    public static void checkPointer(boolean expression)
    {
        if (!expression)
        {
            throw new NullPointerException();
        }
    }

    /**
     * Ensures the truth of an expression.
     *
     * @throws NullPointerException if {@code expression} is false;
     */
    public static void checkPointer(boolean expression, @Nullable String message)
    {
        if (!expression)
        {
            throw new NullPointerException(message);
        }
    }
}
