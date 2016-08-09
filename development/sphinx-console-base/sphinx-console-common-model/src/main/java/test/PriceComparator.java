package test;

/**
 * Created by SKuptsov on 05.10.2015.
 */
public class PriceComparator {

    private PriceComparator INSTANCE;

    int i = 0;

    public PriceComparator(){
        INSTANCE = this;
    }

    public PriceComparator getINSTANCE(){
        return this;
    }


    public static void main(String[] args) {
        PriceComparator bn = new PriceComparator();

        PriceComparator f = bn.getINSTANCE();

        f.i++;

        System.out.println(bn.i);
    }
}
