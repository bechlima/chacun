package ch.epfl.chacun.MyTest;

import ch.epfl.chacun.Base32;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class MyBase32Test {

    @Test
    public void testIsValidWithValidStrings() {
        assertTrue(Base32.isValid("AB"));
        assertTrue(Base32.isValid("234567"));
        assertTrue(Base32.isValid("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"));
        assertTrue(Base32.isValid(""));
    }

    @Test
    public void testIsValidWithInvalidStrings() {
        assertFalse(Base32.isValid("AB01"));
        assertFalse(Base32.isValid("abcdefghijklmnopqrstuvwxyz"));
        assertFalse(Base32.isValid("AB C"));
        assertFalse(Base32.isValid(null));
    }

    @Test
    public void testEncodeBits5WithValidInputs() {
        assertEquals("A", Base32.encodeBits5(0));
        assertEquals("B", Base32.encodeBits5(1));
        assertEquals("7", Base32.encodeBits5(31));
    }

    @Test
    public void testEncodeBits5WithInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> Base32.encodeBits5(-1));
        assertThrows(IllegalArgumentException.class, () -> Base32.encodeBits5(32));
    }

    @Test
    public void testEncodeBits10WithValidInputs() {
        assertEquals("AA", Base32.encodeBits10(0));
        assertEquals("AB", Base32.encodeBits10(1));
        assertEquals("RL", Base32.encodeBits10(555));
        assertEquals("77", Base32.encodeBits10(1023));
    }

    @Test
    public void testEncodeBits10WithInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> Base32.encodeBits10(-1));
        assertThrows(IllegalArgumentException.class, () -> Base32.encodeBits10(1024));
    }

    @Test
    public void testDecodeWithValidSingleCharacterStrings() {
        assertEquals(0, Base32.decode("A"));
        assertEquals(1, Base32.decode("B"));
        assertEquals(31, Base32.decode("7"));
    }

    @Test
    public void testDecodeWithValidTwoCharacterStrings() {
        assertEquals(32, Base32.decode("BA"));
        assertEquals(63, Base32.decode("B7"));
        assertEquals(1023, Base32.decode("77"));
    }

    @Test
    public void testDecodeWithInvalidStrings() {
        assertThrows(IllegalArgumentException.class, () -> Base32.decode(""));
        assertThrows(IllegalArgumentException.class, () -> Base32.decode("1"));
        assertThrows(IllegalArgumentException.class, () -> Base32.decode("AB1"));
        assertThrows(IllegalArgumentException.class, () -> Base32.decode("123"));
        assertThrows(IllegalArgumentException.class, () -> Base32.decode("ZZZ"));
    }
}