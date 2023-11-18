package me.ericjohns55.cryptography;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import me.ericjohns55.cryptography.ciphers.Ciphers;
import me.ericjohns55.cryptography.ciphers.Flags;
import me.ericjohns55.cryptography.macros.AtbashMacroItem;
import me.ericjohns55.cryptography.macros.CaesarMacroItem;
import me.ericjohns55.cryptography.macros.Macro;
import me.ericjohns55.cryptography.macros.MacroItem;
import me.ericjohns55.cryptography.macros.SubstitutionMacroItem;
import me.ericjohns55.cryptography.macros.VigenereMacroItem;

public class MacrosTest {
    @Test
    public void testMacro1() {
        Flags flags = new Flags();

        MacroItem test1 = new CaesarMacroItem(4, flags);
        MacroItem test2 = new SubstitutionMacroItem(Ciphers.generateAlphabet(), flags);
        MacroItem test3 = new VigenereMacroItem("security", flags);
        MacroItem test4 = new AtbashMacroItem(flags);
        MacroItem test5 = new CaesarMacroItem(8, flags);

        Macro macro = new Macro();
        macro.addMacroItem(test1);
        macro.addMacroItem(test2);
        macro.addMacroItem(test3);
        macro.addMacroItem(test4);
        macro.addMacroItem(test5);

        String input = "the quick brown fox jumps over the lazy dog";
        macro.runMacro(input, true);
        macro.runMacro(macro.getOutput(), false);
        assertEquals(input, macro.getOutput());
    }

    @Test
    public void testMacro2() {
        Flags flags = new Flags();

        MacroItem test1 = new CaesarMacroItem(4, flags);
        MacroItem test2 = new CaesarMacroItem(8, flags);

        Macro macro = new Macro();
        macro.addMacroItem(test1);
        macro.addMacroItem(test2);

        String input = "the quick brown fox jumps over the lazy dog";
        macro.runMacro(input, true);

        String rot12 = Ciphers.caesarCipher(input, 12, flags);
        assertEquals(macro.getOutput(), rot12);
    }

    @Test
    public void testMacro3() {
        Flags flags = new Flags();

        MacroItem test1 = new AtbashMacroItem(flags);
        MacroItem test2 = new AtbashMacroItem(flags);

        Macro macro = new Macro();
        macro.addMacroItem(test1);
        macro.addMacroItem(test2);

        String input = "the quick brown fox jumps over the lazy dog";
        macro.runMacro(input, true);

        assertEquals(input, macro.getOutput());
    }

    @Test
    public void testMacro4() {
        Flags flags = new Flags();

        String alphabet = Ciphers.generateAlphabet();
        MacroItem test1 = new SubstitutionMacroItem(alphabet, flags);

        Macro macro = new Macro();
        macro.addMacroItem(test1);

        String input = "the quick brown fox jumps over the lazy dog";
        macro.runMacro(input, false);

        String testCase = Ciphers.substitutionCipher(input, alphabet, flags, true);

        assertEquals(testCase, macro.getOutput());
    }

    @Test
    public void testMacro5() {
        Flags flags = new Flags();

        String key = Ciphers.generateKey();
        MacroItem test1 = new VigenereMacroItem(key, flags);
        MacroItem test2 = new AtbashMacroItem(flags);

        Macro macro = new Macro();
        macro.addMacroItem(test1);
        macro.addMacroItem(test2);

        String input = "the quick brown fox jumps over the lazy dog";
        macro.runMacro(input, true);

        input = Ciphers.vigenereCipher(input, key, flags, true);
        input = Ciphers.atbashCipher(input, flags);


        assertEquals(input, macro.getOutput());
    }

    @Test
    public void testMacro6() {
        Flags flags = new Flags();

        MacroItem test1 = new CaesarMacroItem(4, flags);
        MacroItem test2 = new SubstitutionMacroItem(Ciphers.generateAlphabet(), flags);
        MacroItem test3 = new VigenereMacroItem("security", flags);
        MacroItem test4 = new AtbashMacroItem(flags);
        MacroItem test5 = new CaesarMacroItem(8, flags);

        Macro macro = new Macro();
        macro.addMacroItem(test1);
        macro.addMacroItem(test2);
        macro.addMacroItem(test3);
        macro.addMacroItem(test4);
        macro.addMacroItem(test5);

        assertTrue(macro.validate());
    }

    @Test
    public void testMacro7() {
        Flags flags = new Flags();

        MacroItem test1 = new CaesarMacroItem(4, flags);
        MacroItem test2 = new SubstitutionMacroItem("ABCDEFGHIJKLMNOPQRSTUVWXYZA", flags);
        MacroItem test3 = new VigenereMacroItem("security", flags);
        MacroItem test4 = new AtbashMacroItem(flags);
        MacroItem test5 = new CaesarMacroItem(8, flags);

        Macro macro = new Macro();
        macro.addMacroItem(test1);
        macro.addMacroItem(test2);
        macro.addMacroItem(test3);
        macro.addMacroItem(test4);
        macro.addMacroItem(test5);

        assertFalse(macro.validate());
    }

    @Test
    public void testMacro8() {
        Flags flags = new Flags();

        String alphabet = Ciphers.generateAlphabet();
        String key = Ciphers.generateKey();

        MacroItem test1 = new CaesarMacroItem(4, flags);
        MacroItem test2 = new SubstitutionMacroItem(alphabet, flags);
        MacroItem test3 = new VigenereMacroItem(key, flags);
        MacroItem test4 = new AtbashMacroItem(flags);

        Macro macro = new Macro();
        macro.addMacroItem(test1);
        macro.addMacroItem(test2);
        macro.addMacroItem(test3);
        macro.addMacroItem(test4);

        String input = "the quick brown fox jumps over the lazy dog";
        macro.runMacro(input, true);

        input = Ciphers.caesarCipher(input, 4, flags);
        input = Ciphers.substitutionCipher(input, alphabet, flags, false);
        input = Ciphers.vigenereCipher(input, key, flags, true);
        input = Ciphers.atbashCipher(input, flags);

        assertEquals(input, macro.getOutput());
    }
}
