package test;

/**
 * Created by SKuptsov on 05.10.2015.
 */
public class Main {

    public static void main(String[] args) {

        System.out.println(new Main().getText());


    }

    protected String getText() {
        StringBuffer a = new StringBuffer();

        if (getName() != null) {
            a.append("feed name " + getName() + " - ");
        }

        return a.toString();
    }

    public static String getName() {
        return null;
    }
}
