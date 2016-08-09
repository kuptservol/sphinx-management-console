package test;

/**
 * Created by SKuptsov on 05.10.2015.
 */
// Result :  2 O(N)
public class ReverseWords {


    private static String reverseWords(String word) {
        char[] words = word.toCharArray();

        //O(N) cycle
        for (int i = 0; i < words.length / 2; i++) {
            char tmp = words[words.length - 1 - i];
            words[words.length - 1 - i] = words[i];
            words[i] = tmp;
        }

        System.out.println(new String(words));

        int prevWordStart = 0;
        for (int i = 0; i < words.length; i++) {
            if (Character.isWhitespace(words[i]) || i==words.length-1) {

                for (int j = prevWordStart; j < prevWordStart+(i-prevWordStart)/2; j++) {
                    char tmp = words[j];
                    words[j] = words[i-j+prevWordStart-1];
                    words[i-j+prevWordStart-1] = tmp;
                }

                prevWordStart = i + 1;
            }
        }


        System.out.println(new String(words));

        return "";
    }


    public static void main(String[] args) {
        System.out.println(ReverseWords.reverseWords("Шла Саша по шоссе"));
    }
}
