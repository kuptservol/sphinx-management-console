package test;

import java.util.Random;

/**
 * Created by SKuptsov on 05.10.2015.
 */
public class Test2 {

    public static void main(String[] args) {

        System.out.println(Test2.getPi(10000000));

    }

    public static double getPi(long max_point)
    {

        double x, y, Pi;
        long radius = 1;

        double N_in_circle = 0;

        for(int i =0 ; i < max_point; i++ )
        {
            Random generator = new Random();

            x = generator.nextDouble();
            y = generator.nextDouble();

            if (x*x+y*y < radius)
                N_in_circle++;
        }


       return (N_in_circle/max_point)*4;

    }




}
