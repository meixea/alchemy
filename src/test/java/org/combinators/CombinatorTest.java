package org.combinators;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CombinatorTest {
    @Test
    void combinator(){
        Combinator combo = new Combinator(6, 3);
        assertEquals(20, combo.stream().count());

        combo = new Combinator(10, 4);
        assertEquals(210, combo.stream().count());
    }
}
