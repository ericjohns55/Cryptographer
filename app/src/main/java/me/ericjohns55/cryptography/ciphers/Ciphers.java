package me.ericjohns55.cryptography.ciphers;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Provides encoding methods for a Caesar, Substitution, Vigenère, and Atbash cipher.
 *
 * @author Eric Johns
 */

public enum Ciphers {
    CAESAR,
    SUBSTITUTION,
    VIGENERE,
    ATBASH;

    /**
     * Converts a Cipher type into a formatted string representation
     */
    public static String typeToText(Ciphers type) {
        switch (type) {
            case CAESAR:
                return "Caesar";
            case SUBSTITUTION:
                return "Substitution";
            case VIGENERE:
                return "Vigenère";
            case ATBASH:
                return "Atbash";
            default:
                return null;
        }
    }

    /**
     * Encodes a message using a Caesar (shift) cipher
     * @param message The message to encode
     * @param rotate The amount to rotate the message by
     * @param flags Extra encoding flags
     * @return Encoded text after the Cipher is performed
     */
    public static String caesarCipher(String message, int rotate, Flags flags) {
        // converts the input text into the output text case
        message = parseCase(message, flags.getTextCase());

        StringBuilder cipherText = new StringBuilder();

        for (int i = 0; i < message.length(); i++) {
            char currentSymbol = message.charAt(i);

            if (Character.isLetter(currentSymbol)) { // if it is a letter, shift it
                cipherText.append(shiftLetter(currentSymbol, rotate));
            } else if (Character.isSpaceChar(currentSymbol)) { // preserve whitespace
                cipherText.append(currentSymbol);
            }  else {
                // shift digits if the flags permit
                if (Character.isDigit(currentSymbol) && flags.rotateNumbers()) {
                    cipherText.append(shiftDigit(currentSymbol, rotate));
                } else if (flags.preserveCharacters()) {
                    // add the untouched digit if we are preserving characters
                    cipherText.append(currentSymbol);
                }
            }
        }

        return cipherText.toString();
    }

    /**
     * Rotates a  character by the given amount
     * Takes uppercase and lowercase letters into account
     * @param original The character to rotate
     * @param rotate The amount to rotate by
     * @return The shifted letter normalized into the correct text case
     */
    private static char shiftLetter(char original, int rotate) {
        if (!Character.isLetter(original)) { // ignore if a letter isnt provided
            return original;
        }

        // calculate the shift amount on a 0-26 scale
        int newVal = original + (rotate % 26);

        // default to upper case letters while calculating the bounds
        char lowerBound = 'A', upperBound = 'Z';

        if (original >= 'a' && original <= 'z') { // updating bounds if lowercase
            lowerBound = 'a';
            upperBound = 'z';
        }

        // adjust the character if it exited the alphabets bounds in the ASCII table
        if (newVal > upperBound) {
            newVal -= 26;
        } else if (newVal < lowerBound) {
            newVal += 26;
        }

        return (char) newVal;
    }

    /**
     * Rotates a digit by the given amount
     * @param original The character to rotate
     * @param rotate The amount to rotate by
     * @return The shifted letter normalized from 0-9 in the ASCII table
     */
    private static char shiftDigit(char original, int rotate) {
        if (!Character.isDigit(original)) { // ignore non digits
            return original;
        }

        // calculate the shift amount on a 0-9 scale
        int newVal = original + (rotate % 10);

        // if we go out of bounds in the ASCII table, adjust up 10 or down 10
        if (newVal < '0') {
            newVal += 10;
        } else if (newVal > '9') {
            newVal -= 10;
        }

        return (char) newVal;
    }

    /**
     * Generates a random rotate amount for a Caesar cipher
     * @return Random rotate amount from 1-25
     */
    public static int generateRotateAmount() {
        int number = (int) (Math.random() * 25) + 1;
        if (Math.random() < 0.5) number *= -1; // 50% chance to be negative
        return number;
    }

    /**
     * Performs a Substitution (replacement) cipher on the given input
     * validAlphabet() should be called as a precondition to this function
     * @param message The input text to encode
     * @param alphabet The alphabet to replace each character with
     * @param flags Extra encoding flags
     * @param flipMap Set to true if we are decoding (reverse looking up the alphabet),
     *               false if we are encoding as normal
     * @return The encoded/decoded text
     */
    public static String substitutionCipher(String message, String alphabet,
                                            Flags flags, boolean flipMap) {
        // converts the input text into the output text case
        message = parseCase(message, flags.getTextCase());

        // cannot encode an invalid alphabet, though validAlphabet() should be called
        // first
        if (alphabet.length() != 26) {
            return null;
        }

        StringBuilder cipherText = new StringBuilder();

        // holds the lookup values for the alphabet
        HashMap<Character, Character> alphabetValues = new HashMap<>();

        for (int i = 0; i < alphabet.length(); i++) {
            if (flipMap) { // for decoding, put the alphabet value in the key position
                alphabetValues.put(Character.toUpperCase(alphabet.charAt(i)), (char) (65 + i));
            } else { // for encoding, put A-Z in the key position
                alphabetValues.put((char) (65 + i), Character.toUpperCase(alphabet.charAt(i)));
            }
        }

        for (int i = 0; i < message.length(); i++) {
            char currentValue = Character.toUpperCase(message.charAt(i));

            if (alphabetValues.containsKey(currentValue)) {
                boolean upperCase = message.charAt(i) <= 90;

                // this says that a the alphabetValues lookup could be null, but as
                // long as validAlphabet was called first this should not happen
                if (upperCase) {
                    cipherText.append((char) alphabetValues.get(currentValue));
                } else {
                    // lowercase letters are 32 places above uppercase letters in ASCII
                    cipherText.append((char) (alphabetValues.get(currentValue) + 32));
                }
            } else {
                // preserve whitespace always; characters only if the flags say so
                if (flags.preserveCharacters() || Character.isSpaceChar(currentValue)) {
                    cipherText.append(currentValue);
                }
            }
        }

        return cipherText.toString();
    }

    /**
     * Returns true if an alphabet is valid
     * An alphabet is valid if it contains all 26 letters only once
     * @param alphabet The alphabet to check
     * @return True if the parameter is valid, false otherwise
     */
    public static boolean validAlphabet(String alphabet) {
        if (alphabet.length() != 26) { // a valid alphabet must be 26 characters
            return false;
        }

        String fullAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        alphabet = alphabet.toUpperCase();

        for (int i = 0; i < fullAlphabet.length(); i++) {
            if (!alphabet.contains(Character.toString(fullAlphabet.charAt(i)))) {
                return false; // alphabet cannot be valid if a letter is missing
            }
        }

        return true;
    }

    /**
     * Generates a random alphabet for a substitution cipher
     * @return A valid alphabet for a substitution cipher
     */
    public static String generateAlphabet() {
        String fullAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        List<String> letters = Arrays.asList(fullAlphabet.split(""));
        Collections.shuffle(letters);

        StringBuilder newAlphabet = new StringBuilder();
        for (String letter : letters) {
            newAlphabet.append(letter);
        }

        return newAlphabet.toString();
    }

    /**
     * Encodes an input message with a Vigenère cipher
     * @param message The message to encode
     * @param key The key to encode with
     * @param flags Encoding flags
     * @param encode True if we are encoding with the key, false if we are decoding
     *               with the key
     * @return The encoded/decoded text
     */
    public static String vigenereCipher(String message, String key, Flags flags,
                                        boolean encode) {
        // converts the input text into the output text case
        message = parseCase(message, flags.getTextCase());

        // cannot encode an invalid key (must have at least one character)
        if (key == null || key.length() == 0) {
            return null;
        }

        char[] keyArray = key.toUpperCase().toCharArray();

        StringBuilder cipherText = new StringBuilder();

        // index of where we are in the key (this will repeat many times)
        int keyIndex = 0;

        for (int i = 0; i < message.length(); i++) {
            char currentSymbol = message.charAt(i);

            // a Caesar cipher is performed for each character depending on the key
            int rotateAmount = keyArray[keyIndex] - 'A';
            if (!encode) rotateAmount *= -1; // for decoding we rotate backwards

            // ensures we do not waste a key index on whitespace where it does nothing
            if (Character.isLetter(currentSymbol)) {
                cipherText.append(shiftLetter(currentSymbol, rotateAmount));
                keyIndex++;
            } else { // rotate digits only if the flags permit
                if (Character.isDigit(currentSymbol) && flags.rotateNumbers()) {
                    cipherText.append(shiftDigit(currentSymbol, rotateAmount));
                    keyIndex++;
                } else if (Character.isSpaceChar(currentSymbol) || flags.preserveCharacters()) {
                    // preserve whitespace always; characters if flags permit
                    cipherText.append(currentSymbol);
                }
            }

            // if we hit the end of the key, set the index to 0 so it repeats
            if (keyIndex >= keyArray.length) {
                keyIndex = 0;
            }
        }

        return cipherText.toString();
    }

    /**
     * Generates a random key of length 3-12 for a Vigenère cipher
     * @return The random key
     */
    public static String generateKey() {
        String fullAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder keyBuilder = new StringBuilder();
        int length = (int) (Math.random() * 10) + 3;

        for (int i = 0; i < length; i++) {
            int letterIndex = (int) (Math.random() * 26);
            keyBuilder.append(fullAlphabet.charAt(letterIndex));
        }

        return keyBuilder.toString();
    }

    /**
     * Encodes a message using an Atbash cipher
     * @param message The message to encode
     * @param flags Encoding flags
     * @return The encoded message
     */
    public static String atbashCipher(String message, Flags flags) {
        // converts the input text into the output text case
        message = parseCase(message, flags.getTextCase());
        StringBuilder cipherText = new StringBuilder();

        for (int i = 0; i < message.length(); i++) {
            char currentValue = message.charAt(i);

            if (Character.isLetter(currentValue)) {
                char newVal;

                // A turns to Z, B to Y, etc.
                // we do this by subtracting difference between the value and A from
                // the end of the alphabet
                if (currentValue <= 'Z') {
                    newVal = (char) ('Z' - (currentValue - 'A'));
                } else {
                    newVal = (char) ('z' - (currentValue - 'a'));
                }

                cipherText.append(newVal);
            } else if (Character.isSpaceChar(currentValue)) {
                // preserve whitespace always
                cipherText.append(currentValue);
            }  else {
                if (Character.isDigit(currentValue) && flags.rotateNumbers()) {
                    // digits flip from 0 to 9, 1 to 8, etc for numbers
                    char newVal = (char) ('9' - (currentValue - '0'));
                    cipherText.append(newVal);
                } else if (flags.preserveCharacters()) {
                    cipherText.append(currentValue);
                }
            }
        }

        return cipherText.toString();
    }

    /**
     * Counts the number of occurrences of each letter in an input string
     * @param input String to count the digits of
     * @param groupLength The length of an encoding group
     * @param groupNum The group number within a series of encoding groups
     * @return A HashMap containing the number of occurrences for each letter and a '#'
     * entry with the number of total alphabetic characters
     */
    public static HashMap<Character, Integer> getFrequency(String input,
                                                           int groupLength,
                                                           int groupNum) {
        input = input.toUpperCase();

        HashMap<Character, Integer> map = new HashMap<>();

        // populate the map with all characters from A to Z
        for (char i = 'A'; i <= 'Z'; i++) {
            map.put(i, 0);
        }

        // if the groupLength or groupNumber is 0, strip all non-alphabetic characters
        if (groupLength == -1 || groupNum == -1) {
            input = input.replaceAll("[^a-zA-Z]+", "");
        }

        // accumulator variable for character count
        int characterCount = 0;

        for (int i = 0; i < input.length(); i++) {
            char current = input.charAt(i);

            // if the group length is -1, count as normal
            // otherwise, we must be at the correct position in the groupLength
            // to count the frequency accurately
            if (Character.isLetter(current) &&
                    ((i % groupLength == groupNum) || groupLength == -1)) {
                map.put(current, map.get(current) + 1);
                characterCount++;
            }
        }

        // put count of valid characters in # character for reference in frequency
        // analysis
        map.put('#', characterCount);

        return map;
    }

    /**
     * Converts a string to the correct resulting TextCase
     * @param message Input message
     * @param textCase TextCase to convert to
     * @return The input message with parsed text case
     */
    private static String parseCase(String message, TextCases textCase) {
        if (textCase == TextCases.ALL_UPPER) {
            return message.toUpperCase();
        } else if (textCase == TextCases.ALL_LOWER) {
            return message.toLowerCase();
        } else {
            return message;
        }
    }
}
