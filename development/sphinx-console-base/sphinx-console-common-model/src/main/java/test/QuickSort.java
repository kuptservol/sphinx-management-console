package test;

/**
 * Created by SKuptsov on 24.09.2015.
 */
public class QuickSort {


    public static void main(String[] args) {

        int[] a = {1,56,78,4,4,8,8,8,8,56,457};

        quickSort(a);

        for(int i : a)
            System.out.print(" " + i);
    }

    private static void quickSort(int[] a) {

        sort(a, 0, a.length - 1);
    }

    private static void sort(int[] a, int lo, int hi) {

        if(hi<=lo)
            return;

        int j = partition(a, lo, hi);
        sort(a, lo, j-1);
        sort(a, j+1, hi);


    }

    private static int partition(int[] a, int lo, int hi) {

        int i = lo; int j = hi+1;
        int v = a[lo];
        while(true)
        {
            while(a[++i] < v) if(i==hi) break;
            while(a[--j] > v) if(j==lo) break;
            if(i>=j)
                break;

            int t = a[i];
            a[i]=a[j];
            a[j]=t;
        }

        int t = a[lo];
        a[lo]=a[j];
        a[j]=t;
        return j;
    }
}
