package test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by SKuptsov on 30.09.2015.
 */
public class Test {


    public static void main(String[] args) {

       int[] massvie = {1,2,3,6,7,8};

        long N1=0;
        long N2=1;

        for(int i = 0; i < massvie.length; i++)
        {
            N1=N1+=i;
            N2=N2*=i;
        }



    }


}
