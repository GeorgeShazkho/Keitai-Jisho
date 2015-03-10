package cl.shazkho.utils.keitaijisho.tools;

import java.util.HashMap;

/**
 * *************************************
 * PROJECT: KEITAI JISHO
 * MODULE:  JapaneseWritingHelper
 * Package: cl.shazkho.utils.keitaijisho.tools
 * ***************************************
 * Helper class to hel manage japanese writing systems.
 * ***************************************
 * IMPORTANT AUTHOR INFORMATION!!
 *
 * Some of the functions implemented on the current script ARE NOT MY OWN WORK.
 * In particular, methods 'toRoma', 'katakanaToHiragana' and 'prepareJtoR' are
 * part of the project WANA KANA JAVA, MIT Licensed by Matthew Miller on 2013.
 *
 * @author Matthew Miller
 * @author George Shazkho
 * @version 0.7 - March 09, 2015
 */
public class JapaneseWritingHelper implements StaticHelpers {

    /**
     * KEITAI JISHO PROJECT VARIABLES
     */
    static public final int HIRAGANA_LOWER_BOUND = 12353;
    static public final int HIRAGANA_UPPER_BOUND = 12447;
    static public final int KATAKANA_LOWER_BOUND_BASE = 12448;
    static public final int KATAKANA_UPPER_BOUND_BASE = 12543;
    static public final int KATAKANA_LOWER_BOUND_EXT = 12784;
    static public final int KATAKANA_UPPER_BOUND_EXT = 12799;
    static public final int KANJI_LOWER_BOUND_BASE = 19968;
    static public final int KANJI_UPPER_BOUND_BASE = 40879;
    static public final int KANJI_LOWER_BOUND_RARE = 13312;
    static public final int KANJI_UPPER_BOUND_RARE = 19903;

    /**
     * WANA KANA JAVA PROJECT VARIABLES
     */
    static final int HIRAGANA_START = 0x3041;
    static final int KATAKANA_START = 0x30A1;
    static HashMap<String, String> mJtoR = new HashMap<>();


    // KEITAI JISHO PROJECT METHODS

    /**
     * Returns if the char is one in the HIRAGANA unicode range.
     *
     * @param test The char to be tested.
     * @return True if the character is in hiragana range.
     */
    static public boolean isHiraganaCharacter(char test) {
        String unicode_value = Integer.toHexString( test );
        int int_value = Integer.parseInt(unicode_value, 16);
        return HIRAGANA_LOWER_BOUND <= int_value && HIRAGANA_UPPER_BOUND >= int_value;
    }

    /**
     * Returns if the char is one in the KATAKANA unicode range.
     *
     * @param test The char to be tested.
     * @return True if the character is in katakana range.
     */
    static public boolean isKatakanaCharacter(char test) {
        String unicode_value = Integer.toHexString( test );
        int int_value = Integer.parseInt(unicode_value, 16);
        return  ( KATAKANA_LOWER_BOUND_BASE <= int_value && KATAKANA_UPPER_BOUND_BASE >= int_value ) ||
                ( KATAKANA_LOWER_BOUND_EXT  <= int_value && KATAKANA_UPPER_BOUND_EXT  >= int_value );
    }

    /**
     * Returns if the char is one in the KANJI unicode range.
     *
     * @param test The char to be tested.
     * @return True if the character is in kanji range.
     */
    static public boolean isKanjiCharacter(char test) {
        String unicode_value = Integer.toHexString( test );
        int int_value = Integer.parseInt(unicode_value, 16);
        return  ( KANJI_LOWER_BOUND_BASE <= int_value && KANJI_UPPER_BOUND_BASE >= int_value ) ||
                ( KANJI_LOWER_BOUND_RARE  <= int_value && KANJI_UPPER_BOUND_RARE  >= int_value );
    }

    /**
     * Returns if the char is in the traditional roman list for ROMAJI
     *
     * @param test The char to be tested.
     * @return True if the character is in romaji char list.
     */
    static public boolean isRomajiCharacter(char test) {
        return "abcdefghijklmnopqrstuvwxyzāēūōêôûâ".contains(""+test);
    }

    /**
     * Determines if the String is merely made of HIRAGANA characters.
     *
     * @param test The string to be tested.
     * @return True is all characters in the String are hiragana chars.
     */
    static public boolean isHiragana(String test) {
        for (int i = 0; i < test.length(); ++i) {
            if ( !isHiraganaCharacter( test.charAt(i) ) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the String is merely made of KATAKANA characters.
     *
     * @param test The string to be tested.
     * @return True is all characters in the String are katakana chars.
     */
    static public boolean isKatakana(String test) {
        for (int i = 0; i < test.length(); ++i) {
            if ( !isKatakanaCharacter( test.charAt(i) ) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the String is merely made of KANJI characters.
     *
     * @param test The string to be tested.
     * @return True is all characters in the String are kanji or hiragana chars.
     */
    static public boolean isKanji(String test) {
        for (int i = 0; i < test.length(); ++i) {
            if ( !isHiraganaCharacter( test.charAt(i) ) && !isKanjiCharacter( test.charAt(i) ) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the String is merely made of ROMAJI characters.
     *
     * @param test The String to be tested.
     * @return True is all characters in the String are romaji chars.
     */
    static public boolean isRomaji(String test) {
        for (int i = 0; i < test.length(); ++i) {
            if ( !isRomajiCharacter( test.charAt(i) ) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if a String is of any of the accepted Japanese Writing Systems.
     *
     * @param test The String to be tested.
     * @return An String corresponding to the writing system found. "mixed" if not valid.
     */
    static public String isSomething(String test) {
        if ( isHiragana( test ) || isKatakana( test ) ) {
            return "reb";
        } else if ( isKanji( test ) ) {
            return "keb";
        } else if ( isRomaji( test ) ) {
            return "kana";
        } else {
            return "mixed";
        }
    }


    // WANA KANA JAVA PROJECT METHODS (slightly modified to fit my project)
    // No original documentation was presented.

    /**
     * Converts KANA written strings into ROMAJI.
     *
     * @param text The String for conversion.
     * @return Romaji-equivalent String of the provided KANA String.
     */
    public static String toRomaji(String text) {

        // If the auxiliary array is not initialized, do it.
        if ( mJtoR.size() == 0 ) {
            prepareJtoR();
        }
        // If the String is already Romaji, return it (weird but can happen).
        if( isRomaji( text ) ) {
            return text;
        }

        // Unmodified original code from WANA KANA JAVA
        String chunk = "";
        int chunkSize;
        int cursor = 0;
        int len = text.length();
        int maxChunk = 2;
        boolean nextCharIsDoubleConsonant = false;
        String roma = "";
        String romaChar = null;

        while (cursor < len) {
            chunkSize = Math.min(maxChunk, len - cursor);
            while (chunkSize > 0) {
                chunk = text.substring(cursor, (cursor+chunkSize));
                if (isKatakana(chunk)) {
                    chunk = katakanaToHiragana(chunk);
                }
                if (String.valueOf(chunk.charAt(0)).equals("っ") && chunkSize == 1 && cursor < (len - 1)) {
                    nextCharIsDoubleConsonant = true;
                    romaChar = "";
                    break;
                }
                romaChar = mJtoR.get(chunk);
                if ((romaChar != null) && nextCharIsDoubleConsonant) {
                    romaChar += romaChar.charAt(0);
                    nextCharIsDoubleConsonant = false;
                }
                if (romaChar != null) {
                    break;
                }
                chunkSize--;
            }
            if (romaChar == null) {
                romaChar = chunk;
            }
            roma += romaChar;
            cursor += chunkSize > 0 ? chunkSize : 1;
        }
        return roma;
    }

    /**
     * Converts a String in KATAKANA to another in HIRAGANA.
     *
     * @param kata The String to be converted.
     * @return The Hiragana-equivalent String of the Katakana input.
     */
    private static String katakanaToHiragana(String kata) {
        // Unmodified original code from WANA KANA JAVA
        int code;
        String hiragana = "";
        for (int _i = 0; _i < kata.length(); _i++) {
            char kataChar = kata.charAt(_i);

            if (isKatakanaCharacter(kataChar)) {
                code = (int) kataChar;
                code += HIRAGANA_START - KATAKANA_START;
                hiragana += String.valueOf(Character.toChars(code));
            }
            else {
                hiragana += kataChar;
            }
        }
        return hiragana;
    }

    /**
     * Auxiliary array initialization.
     */
    private static void prepareJtoR() {
        // Unmodified original code from WANA KANA JAVA
        mJtoR.put("あ", "a");
        mJtoR.put("い", "i");
        mJtoR.put("う", "u");
        mJtoR.put("え", "e");
        mJtoR.put("お", "o");
        mJtoR.put("ゔぁ", "va");
        mJtoR.put("ゔぃ", "vi");
        mJtoR.put("ゔ", "vu");
        mJtoR.put("ゔぇ", "ve");
        mJtoR.put("ゔぉ", "vo");
        mJtoR.put("か", "ka");
        mJtoR.put("き", "ki");
        mJtoR.put("きゃ", "kya");
        mJtoR.put("きぃ", "kyi");
        mJtoR.put("きゅ", "kyu");
        mJtoR.put("く", "ku");
        mJtoR.put("け", "ke");
        mJtoR.put("こ", "ko");
        mJtoR.put("が", "ga");
        mJtoR.put("ぎ", "gi");
        mJtoR.put("ぐ", "gu");
        mJtoR.put("げ", "ge");
        mJtoR.put("ご", "go");
        mJtoR.put("ぎゃ", "gya");
        mJtoR.put("ぎぃ", "gyi");
        mJtoR.put("ぎゅ", "gyu");
        mJtoR.put("ぎぇ", "gye");
        mJtoR.put("ぎょ", "gyo");
        mJtoR.put("さ", "sa");
        mJtoR.put("す", "su");
        mJtoR.put("せ", "se");
        mJtoR.put("そ", "so");
        mJtoR.put("ざ", "za");
        mJtoR.put("ず", "zu");
        mJtoR.put("ぜ", "ze");
        mJtoR.put("ぞ", "zo");
        mJtoR.put("し", "shi");
        mJtoR.put("しゃ", "sha");
        mJtoR.put("しゅ", "shu");
        mJtoR.put("しょ", "sho");
        mJtoR.put("じ", "ji");
        mJtoR.put("じゃ", "ja");
        mJtoR.put("じゅ", "ju");
        mJtoR.put("じょ", "jo");
        mJtoR.put("た", "ta");
        mJtoR.put("ち", "chi");
        mJtoR.put("ちゃ", "cha");
        mJtoR.put("ちゅ", "chu");
        mJtoR.put("ちょ", "cho");
        mJtoR.put("つ", "tsu");
        mJtoR.put("て", "te");
        mJtoR.put("と", "to");
        mJtoR.put("だ", "da");
        mJtoR.put("ぢ", "di");
        mJtoR.put("づ", "du");
        mJtoR.put("で", "de");
        mJtoR.put("ど", "do");
        mJtoR.put("な", "na");
        mJtoR.put("に", "ni");
        mJtoR.put("にゃ", "nya");
        mJtoR.put("にゅ", "nyu");
        mJtoR.put("にょ", "nyo");
        mJtoR.put("ぬ", "nu");
        mJtoR.put("ね", "ne");
        mJtoR.put("の", "no");
        mJtoR.put("は", "ha");
        mJtoR.put("ひ", "hi");
        mJtoR.put("ふ", "fu");
        mJtoR.put("へ", "he");
        mJtoR.put("ほ", "ho");
        mJtoR.put("ひゃ", "hya");
        mJtoR.put("ひゅ", "hyu");
        mJtoR.put("ひょ", "hyo");
        mJtoR.put("ふぁ", "fa");
        mJtoR.put("ふぃ", "fi");
        mJtoR.put("ふぇ", "fe");
        mJtoR.put("ふぉ", "fo");
        mJtoR.put("ば", "ba");
        mJtoR.put("び", "bi");
        mJtoR.put("ぶ", "bu");
        mJtoR.put("べ", "be");
        mJtoR.put("ぼ", "bo");
        mJtoR.put("びゃ", "bya");
        mJtoR.put("びゅ", "byu");
        mJtoR.put("びょ", "byo");
        mJtoR.put("ぱ", "pa");
        mJtoR.put("ぴ", "pi");
        mJtoR.put("ぷ", "pu");
        mJtoR.put("ぺ", "pe");
        mJtoR.put("ぽ", "po");
        mJtoR.put("ぴゃ", "pya");
        mJtoR.put("ぴゅ", "pyu");
        mJtoR.put("ぴょ", "pyo");
        mJtoR.put("ま", "ma");
        mJtoR.put("み", "mi");
        mJtoR.put("む", "mu");
        mJtoR.put("め", "me");
        mJtoR.put("も", "mo");
        mJtoR.put("みゃ", "mya");
        mJtoR.put("みゅ", "myu");
        mJtoR.put("みょ", "myo");
        mJtoR.put("や", "ya");
        mJtoR.put("ゆ", "yu");
        mJtoR.put("よ", "yo");
        mJtoR.put("ら", "ra");
        mJtoR.put("り", "ri");
        mJtoR.put("る", "ru");
        mJtoR.put("れ", "re");
        mJtoR.put("ろ", "ro");
        mJtoR.put("りゃ", "rya");
        mJtoR.put("りゅ", "ryu");
        mJtoR.put("りょ", "ryo");
        mJtoR.put("わ", "wa");
        mJtoR.put("を", "wo");
        mJtoR.put("ん", "n");
        mJtoR.put("ゐ", "wi");
        mJtoR.put("ゑ", "we");
        mJtoR.put("きぇ", "kye");
        mJtoR.put("きょ", "kyo");
        mJtoR.put("じぃ", "jyi");
        mJtoR.put("じぇ", "jye");
        mJtoR.put("ちぃ", "cyi");
        mJtoR.put("ちぇ", "che");
        mJtoR.put("ひぃ", "hyi");
        mJtoR.put("ひぇ", "hye");
        mJtoR.put("びぃ", "byi");
        mJtoR.put("びぇ", "bye");
        mJtoR.put("ぴぃ", "pyi");
        mJtoR.put("ぴぇ", "pye");
        mJtoR.put("みぇ", "mye");
        mJtoR.put("みぃ", "myi");
        mJtoR.put("りぃ", "ryi");
        mJtoR.put("りぇ", "rye");
        mJtoR.put("にぃ", "nyi");
        mJtoR.put("にぇ", "nye");
        mJtoR.put("しぃ", "syi");
        mJtoR.put("しぇ", "she");
        mJtoR.put("いぇ", "ye");
        mJtoR.put("うぁ", "wha");
        mJtoR.put("うぉ", "who");
        mJtoR.put("うぃ", "wi");
        mJtoR.put("うぇ", "we");
        mJtoR.put("ゔゃ", "vya");
        mJtoR.put("ゔゅ", "vyu");
        mJtoR.put("ゔょ", "vyo");
        mJtoR.put("すぁ", "swa");
        mJtoR.put("すぃ", "swi");
        mJtoR.put("すぅ", "swu");
        mJtoR.put("すぇ", "swe");
        mJtoR.put("すぉ", "swo");
        mJtoR.put("くゃ", "qya");
        mJtoR.put("くゅ", "qyu");
        mJtoR.put("くょ", "qyo");
        mJtoR.put("くぁ", "qwa");
        mJtoR.put("くぃ", "qwi");
        mJtoR.put("くぅ", "qwu");
        mJtoR.put("くぇ", "qwe");
        mJtoR.put("くぉ", "qwo");
        mJtoR.put("ぐぁ", "gwa");
        mJtoR.put("ぐぃ", "gwi");
        mJtoR.put("ぐぅ", "gwu");
        mJtoR.put("ぐぇ", "gwe");
        mJtoR.put("ぐぉ", "gwo");
        mJtoR.put("つぁ", "tsa");
        mJtoR.put("つぃ", "tsi");
        mJtoR.put("つぇ", "tse");
        mJtoR.put("つぉ", "tso");
        mJtoR.put("てゃ", "tha");
        mJtoR.put("てぃ", "thi");
        mJtoR.put("てゅ", "thu");
        mJtoR.put("てぇ", "the");
        mJtoR.put("てょ", "tho");
        mJtoR.put("とぁ", "twa");
        mJtoR.put("とぃ", "twi");
        mJtoR.put("とぅ", "twu");
        mJtoR.put("とぇ", "twe");
        mJtoR.put("とぉ", "two");
        mJtoR.put("ぢゃ", "dya");
        mJtoR.put("ぢぃ", "dyi");
        mJtoR.put("ぢゅ", "dyu");
        mJtoR.put("ぢぇ", "dye");
        mJtoR.put("ぢょ", "dyo");
        mJtoR.put("でゃ", "dha");
        mJtoR.put("でぃ", "dhi");
        mJtoR.put("でゅ", "dhu");
        mJtoR.put("でぇ", "dhe");
        mJtoR.put("でょ", "dho");
        mJtoR.put("どぁ", "dwa");
        mJtoR.put("どぃ", "dwi");
        mJtoR.put("どぅ", "dwu");
        mJtoR.put("どぇ", "dwe");
        mJtoR.put("どぉ", "dwo");
        mJtoR.put("ふぅ", "fwu");
        mJtoR.put("ふゃ", "fya");
        mJtoR.put("ふゅ", "fyu");
        mJtoR.put("ふょ", "fyo");
        mJtoR.put("ぁ", "a");
        mJtoR.put("ぃ", "i");
        mJtoR.put("ぇ", "e");
        mJtoR.put("ぅ", "u");
        mJtoR.put("ぉ", "o");
        mJtoR.put("ゃ", "ya");
        mJtoR.put("ゅ", "yu");
        mJtoR.put("ょ", "yo");
        mJtoR.put("っ", "");
        mJtoR.put("ゕ", "ka");
        mJtoR.put("ゖ", "ka");
        mJtoR.put("ゎ", "wa");
        mJtoR.put("'　'", " ");
        mJtoR.put("んあ", "n'a");
        mJtoR.put("んい", "n'i");
        mJtoR.put("んう", "n'u");
        mJtoR.put("んえ", "n'e");
        mJtoR.put("んお", "n'o");
        mJtoR.put("んや", "n'ya");
        mJtoR.put("んゆ", "n'yu");
        mJtoR.put("んよ", "n'yo");
    }

}
