package test;

import java.io.*;

/**
 * Created by SKuptsov on 30.09.2015.
 */
public class RecursiveDescentParser {

    TextReader textReader;

    public RecursiveDescentParser(InputStream in) {
        this.textReader = new TextReader(in);
    }

    private char getOperator() throws ParseError {

        char op = textReader.peek();
        if (op == '+' || op == '-' || op == '/' || op == '*')
            return textReader.getAnyChar();
        else if (op == TextReader.EOL)
            throw new ParseError("End of line occured while expecting operator");
        else
            throw new ParseError("Operator not found");
    }


    public double expressionValue() throws ParseError {

        char nextChar = textReader.peek();
        boolean minus = false;
        if (nextChar == '-') {
            minus = true;
            textReader.getAnyChar();
        }

        double termValue = getTerm();
        if(minus)
            termValue = - termValue;
        while(textReader.peek() == '+' | textReader.peek() == '-')
        {
            char operator = getOperator();
            double nextTermValue = getTerm();
            if(operator=='+')
                termValue += nextTermValue;
            if(operator=='-')
                termValue -= nextTermValue;
        }

        return termValue;

    }

    public double getTerm() throws ParseError {

        double factorValue = getFactor();

        while(textReader.peek() == '*' | textReader.peek() == '/')
        {
            char operator = getOperator();
            double nextFactorValue = getFactor();
            if(operator=='*')
                factorValue *= nextFactorValue;
            if(operator=='/')
                factorValue /= nextFactorValue;
        }

        return factorValue;
    }

    public double getFactor() throws ParseError {

        char nextChar = textReader.peek();
        if(Character.isDigit(nextChar))
            return textReader.getLong();
        else if(nextChar=='(')
        {
            textReader.getAnyChar();
            double expression=expressionValue();
            if(textReader.peek()!=')')
                throw new ParseError("Right parenthess not found");
            else{
                textReader.getAnyChar();
                return expression;
            }
        }
        else throw new ParseError("Wrong syntax found");
    }

    private static class TextReader {

        private static final char EOF = (char) 0xFFFF;
        public static final char EOL = '\n';
        BufferedReader reader;
        String buffer;
        int pos;

        TextReader(InputStream in) {
            this.reader = new BufferedReader(new InputStreamReader(in));
        }

        char peek() {
            return lookChar();

        }

        char getAnyChar() {
            char ch = lookChar();
            pos++;

            return ch;
        }

        private char lookChar() {

            if (buffer == null || pos > buffer.length())
                fillBuffer();

            if (buffer == null)
                return EOF;
            else if (pos == buffer.length())
                return EOL;
            else {
                while (pos < buffer.length() - 1 && Character.isWhitespace(buffer.charAt(pos))) {
                    pos++;
                }
                return buffer.charAt(pos);
            }
        }

        private boolean isWhiteSpace(char c) {
            return false;
        }

        private void fillBuffer() {
            try {
                buffer = reader.readLine();
            } catch (IOException e) {
                throw new IllegalArgumentException("Errow while attempting to read form an input stream.");
            }
            pos = 0;
        }

        private double getLong() {
            long i = 0;
            while (Character.isDigit(peek()))
                i = 10 * i + Character.digit(getAnyChar(), 10);

            return i;

        }

        public double getDouble() {
            double x = 0;

            return x;
        }
    }

    private static class ParseError extends Exception {
        ParseError(String message) {
            super(message);
        }
    }

    public static void main(String[] args) throws ParseError {

        double value = new RecursiveDescentParser(System.in).expressionValue();
        System.out.println(" Value : " + value);

//        TextReader t = new TextReader(new ByteArrayInputStream("  88  53 + 5".getBytes()));
//        System.out.println(t.getLong());
    }

}
