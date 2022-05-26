package ru.zhuvikin.auth.watermarking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final class StringEncoder {

    private final static List<String> alphabet = Arrays.asList("\0", "А", "Б", "В", "Г", "Д", "Е", "Ё", "Ж", "З", "И",
            "Й", "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч", "Ш", "Щ", "Ъ", "Ы", "Ь", "Э",
            "Ю", "Я", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
            "T", "U", "V", "W", "X", "Y", "Z", ".", " ", "-");

    static int BITS_PER_SYMBOL = (int) Math.ceil(Math.log10((double) alphabet.size()) / Math.log10(2.0d));

    private static Map<Integer, String> codeToCharacterMap = new HashMap<>();
    private static Map<String, Integer> characterToCodeMap = new HashMap<>();

    static {
        for (int i = 0; i < alphabet.size(); i++) {
            codeToCharacterMap.put(i, alphabet.get(i));
            characterToCodeMap.put(alphabet.get(i), i);
        }
    }

    static BitSet encode(String input, int maximumLength) {
        if (input == null || Objects.equals(input, "")) {
            return new BitSet();
        }

        String string = input.substring(0, Math.min(input.length(), maximumLength));
        string = string.toUpperCase();
        char[] chars = string.toCharArray();
        List<Integer> symbols = new ArrayList<>();
        for (char c : chars) {
            String character = Character.toString(c);
            Integer code = characterToCodeMap.get(character);
            if (code == null) {
                throw new IllegalArgumentException("Character '" + character + "' is not allowed");
            }
            symbols.add(code);
        }

        for (int i = 0; i < maximumLength - string.length(); i++) {
            symbols.add(characterToCodeMap.get("\0"));
        }

        StringBuilder binaryString = new StringBuilder();
        for (Integer symbol : symbols) {
            StringBuilder s = new StringBuilder(Integer.toBinaryString(symbol));
            int p = BITS_PER_SYMBOL;
            int g = 0, j = s.length();
            while (g < p - j) {
                g++;
                s.insert(0, "0");
            }
            binaryString.append(s);
        }

        BitSet result = new BitSet();
        char[] bits = binaryString.toString().toCharArray();
        for (int i = 0; i < bits.length; i++) {
            if (bits[i] == '1') {
                result.set(i);
            }
        }
        return result;
    }

    static String decode(BitSet input, int length) {
        List<String> binaryStrings = new ArrayList<>();
        String binaryString = "";
        for (int i = 0; i < length * BITS_PER_SYMBOL; i++) {
            binaryString += input.get(i) ? "1" : "0";
            if ((i + 1) % BITS_PER_SYMBOL == 0 && i > 0) {
                binaryStrings.add(binaryString);
                binaryString = "";
            }
        }

        StringBuilder result = new StringBuilder();
        for (String string : binaryStrings) {
            int code = Integer.parseInt(string, 2);
            String symbol = codeToCharacterMap.get(code);
            if (symbol == null) {
                throw new IllegalArgumentException("Character with code '" + code + "' is not allowed");
            }
            if (!symbol.equals("\0")) {
                result.append(symbol);
            }
        }
        return result.toString();
    }

}
