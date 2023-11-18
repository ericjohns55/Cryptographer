package me.ericjohns55.cryptography;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import me.ericjohns55.cryptography.ciphers.Ciphers;
import me.ericjohns55.cryptography.ciphers.Flags;
import me.ericjohns55.cryptography.ciphers.TextCases;

public class CiphersTest {
    @Test
    public void testCaesar1() {
        String message = "abc";
        int rotate = 2;

        String cipher = Ciphers.caesarCipher(message, rotate, new Flags());
        assertEquals("cde", cipher);
    }

    @Test
    public void testCaesar2() {
        String message = "ABC";
        int rotate = 27;

        String cipher = Ciphers.caesarCipher(message, rotate, new Flags());
        assertEquals("BCD", cipher);
    }

    @Test
    public void testCaesar3() {
        String message = "AbCdE";
        int rotate = 1;

        String cipher = Ciphers.caesarCipher(message, rotate, new Flags());
        assertEquals("BcDeF", cipher);
    }

    @Test
    public void testCaesar4() {
        String message = "AbCdE";
        int rotate = -1;

        String cipher = Ciphers.caesarCipher(message, rotate, new Flags());
        assertEquals("ZaBcD", cipher);
    }

    @Test
    public void testCaesar5() {
        String message = "abcDE";
        int rotate = -3;

        String cipher = Ciphers.caesarCipher(message, rotate, new Flags());
        assertEquals("xyzAB", cipher);
    }

    @Test
    public void testCaesar6() {
        String message = "This is a long test MESSAGE with varying CAPS and stuff!";
        int rotate = 15;

        String cipher = Ciphers.caesarCipher(message, rotate, new Flags());
        assertEquals("Iwxh xh p adcv ithi BTHHPVT lxiw kpgnxcv RPEH pcs hijuu!", cipher);
    }

    @Test
    public void testCaesar7() {
        String message = "Iwxh xh p adcv ithi BTHHPVT lxiw kpgnxcv RPEH pcs hijuu!";
        int rotate = -15;

        Flags flags = new Flags();
        flags.setPreserveCharacters(false);
        String cipher = Ciphers.caesarCipher(message, rotate, flags);
        assertEquals("This is a long test MESSAGE with varying CAPS and stuff", cipher);
    }

    @Test
    public void testCaesar8() {
        String message = "abcde1234567890";
        int rotate = 1;

        Flags flags = new Flags();
        flags.setRotateNumbers(true);
        String cipher = Ciphers.caesarCipher(message, rotate, flags);
        assertEquals("bcdef2345678901", cipher);
    }

    @Test
    public void testCaesar9() {
        String message = "abcde1234567890";
        int rotate = -2;

        Flags flags = new Flags();
        flags.setRotateNumbers(true);
        String cipher = Ciphers.caesarCipher(message, rotate, flags);
        assertEquals("yzabc9012345678", cipher);
    }

    @Test
    public void testCaesar10() {
        String message = "a!bcde,123$45678#90";
        int rotate = -2;

        Flags flags = new Flags();
        flags.setRotateNumbers(true);
        flags.setPreserveCharacters(false);
        String cipher = Ciphers.caesarCipher(message, rotate, flags);
        assertEquals("yzabc9012345678", cipher);
    }

    @Test
    public void testCaesar11() {
        String message = "a!bcde,123$45678#90";
        int rotate = -2;

        Flags flags = new Flags();
        flags.setRotateNumbers(true);
        flags.setPreserveCharacters(false);
        flags.setTextCase(TextCases.ALL_UPPER);
        String cipher = Ciphers.caesarCipher(message, rotate, flags);
        assertEquals("YZABC9012345678", cipher);
    }

    @Test
    public void testCaesar12() {
        String message = "ABC123!@#";
        int rotate = 3;

        Flags flags = new Flags();
        flags.setRotateNumbers(true);
        flags.setPreserveCharacters(false);
        flags.setTextCase(TextCases.ALL_LOWER);
        String cipher = Ciphers.caesarCipher(message, rotate, flags);
        assertEquals("def456", cipher);
    }

    @Test
    public void testSubstitution1() {
        String alphabet = "CDEFGHIJKLMNOPQRSTUVWXYZAB";
        String message = "abcdefghijklmnopqrstuvwxyz";

        String cipher = Ciphers.substitutionCipher(message, alphabet, new Flags(), false);
        assertEquals("cdefghijklmnopqrstuvwxyzab", cipher);
    }

    @Test
    public void testSubstitution2() {
        String alphabet = "cdefgHIJKLMNOPQRSTUVWXYZAB";
        String message = "aBcDeFgHiJkLmNoPqRsTuVwXyZ";

        String cipher = Ciphers.substitutionCipher(message, alphabet, new Flags(), false);
        assertEquals("cDeFgHiJkLmNoPqRsTuVwXyZaB", cipher);
    }

    @Test
    public void testSubstitution3() {
        String alphabet = "zyxwvutsrqponmlkjihgfedcba";
        String message = "the quick brown fox jumps over thirteen lazy dogs";

        String cipher = Ciphers.substitutionCipher(message, alphabet, new Flags(), false);
        assertEquals("gsv jfrxp yildm ulc qfnkh levi gsrigvvm ozab wlth", cipher);
    }

    @Test
    public void testSubstitution4() {
        String alphabet = "zyxwvutsrqponmlkjihgfedcba";
        String message = "gsv jfrxp yildm ulc qfnkh levi gsrigvvm ozab wlth";

        String cipher = Ciphers.substitutionCipher(message, alphabet, new Flags(), true);
        assertEquals("the quick brown fox jumps over thirteen lazy dogs", cipher);
    }

    @Test
    public void testSubstitution5() {
        String alphabet = "zyxwvutsrqpanmlkjihgfedcbaz";
        String message = "the quick brown fox jumps over thirteen lazy dogs";

        String cipher = Ciphers.substitutionCipher(message, alphabet, new Flags(), false);
        assertNull(cipher);
    }

    @Test
    public void testSubstitution6() {
        String alphabet = "CDEFGHIJKLMNOPQRSTUVWXYZAB";
        String message = "abcdefghijklmnopqrstuvwxyz";

        Flags flags = new Flags();
        flags.setTextCase(TextCases.ALL_UPPER);

        String cipher = Ciphers.substitutionCipher(message, alphabet, flags, false);
        assertEquals("CDEFGHIJKLMNOPQRSTUVWXYZAB", cipher);
    }

    @Test
    public void testSubstitution7() {
        String alphabet = "CDEFGHIJKLMNOPQRSTUVWXYZAB";
        String message = "ABCDE FGHIJ KLMNO PQRST UVWXY Z!@#$";

        Flags flags = new Flags();
        flags.setTextCase(TextCases.ALL_LOWER);
        flags.setPreserveCharacters(true);

        String cipher = Ciphers.substitutionCipher(message, alphabet, flags, false);
        assertEquals("cdefg hijkl mnopq rstuv wxyza b!@#$", cipher);
    }

    @Test
    public void testSubstitution8() {
        String alphabet = "CDEFGHIJKLMNOPQRSTUVWXYZAB";
        String message = "ABCDE fghij KLMNO pqrst UVWXY z!@#$";

        Flags flags = new Flags();
        flags.setTextCase(TextCases.PRESERVE_CASE);
        flags.setPreserveCharacters(false);

        String cipher = Ciphers.substitutionCipher(message, alphabet, flags, false);
        assertEquals("CDEFG hijkl MNOPQ rstuv WXYZA b", cipher);
    }

    @Test
    public void testVigenere1() {
        String message = "test message";
        String key = "ace";

        String cipher = Ciphers.vigenereCipher(message, key, new Flags(), true);
        assertEquals("tgwt oisuegg", cipher);
    }

    @Test
    public void testVigenere2() {
        String message = "test message";
        String key = "b";

        Flags flags = new Flags();
        flags.setTextCase(TextCases.ALL_UPPER);

        String cipher = Ciphers.vigenereCipher(message, key, flags, true);
        assertEquals("UFTU NFTTBHF", cipher);
    }

    @Test
    public void testVigenere3() {
        String message = "TEST MESSAGE!@#";
        String key = "superlongkeyfornoreason";

        Flags flags = new Flags();
        flags.setTextCase(TextCases.ALL_LOWER);
        flags.setPreserveCharacters(false);

        String cipher = Ciphers.vigenereCipher(message, key, flags, true);
        assertEquals("lyhx dpgfgqi", cipher);
    }

    @Test
    public void testVigenere4() {
        String message = "TEST message!@#";
        String key = "test";

        Flags flags = new Flags();
        flags.setTextCase(TextCases.PRESERVE_CASE);
        flags.setPreserveCharacters(true);

        String cipher = Ciphers.vigenereCipher(message, key, flags, true);
        assertEquals("MIKM fikltkw!@#", cipher);
    }

    @Test
    public void testVigenere5() {
        String message = "TEST message";
        String key = "test";

        Flags flags = new Flags();
        flags.setTextCase(TextCases.PRESERVE_CASE);

        String cipher = Ciphers.vigenereCipher(message, key, flags, true);
        assertEquals("MIKM fikltkw", cipher);
    }

    @Test
    public void testVigenere6() {
        String message = "TEST12";
        String key = "ace";

        Flags flags = new Flags();
        flags.setTextCase(TextCases.PRESERVE_CASE);
        flags.setRotateNumbers(true);

        String cipher = Ciphers.vigenereCipher(message, key, flags, true);
        assertEquals("TGWT36", cipher);
    }

    @Test
    public void testAtbash1() {
        String message = "abcdefghijklmnopqrstuvwxyz";

        String cipher = Ciphers.atbashCipher(message, new Flags());
        assertEquals("zyxwvutsrqponmlkjihgfedcba", cipher);
    }

    @Test
    public void testAtbash2() {
        String message = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        String cipher = Ciphers.atbashCipher(message, new Flags());
        assertEquals("ZYXWVUTSRQPONMLKJIHGFEDCBA", cipher);
    }

    @Test
    public void testAtbash3() {
        String message = "This is a SENTENCE with case CHANGES to DEMONSTRATE ciphers! YaY";

        String cipher = Ciphers.atbashCipher(message, new Flags());
        assertEquals("Gsrh rh z HVMGVMXV drgs xzhv XSZMTVH gl WVNLMHGIZGV xrksvih! BzB", cipher);
    }

    @Test
    public void testAtbash4() {
        String message = "Gsrh rh z HVMGVMXV drgs xzhv XSZMTVH gl WVNLMHGIZGV xrksvih! BzB";

        String cipher = Ciphers.atbashCipher(message, new Flags());
        assertEquals("This is a SENTENCE with case CHANGES to DEMONSTRATE ciphers! YaY", cipher);
    }

    @Test
    public void testAtbash5() {
        String message = "ABCDE0123456789!@#$";

        Flags flags = new Flags();
        flags.setPreserveCharacters(false);
        flags.setRotateNumbers(true);
        flags.setTextCase(TextCases.ALL_LOWER);

        String cipher = Ciphers.atbashCipher(message, flags);
        assertEquals("zyxwv9876543210", cipher);
    }

    @Test
    public void testAtbash6() {
        String message = "abcde0123456789!@#$";

        Flags flags = new Flags();
        flags.setPreserveCharacters(true);
        flags.setRotateNumbers(false);
        flags.setTextCase(TextCases.ALL_UPPER);

        String cipher = Ciphers.atbashCipher(message, flags);
        assertEquals("ZYXWV0123456789!@#$", cipher);
    }
}
