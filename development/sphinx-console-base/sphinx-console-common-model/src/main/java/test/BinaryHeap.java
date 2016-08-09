package test;

/**
 * Created by SKuptsov on 24.09.2015.
 */
public class BinaryHeap {


    public static void main(String[] args) {

        BinaryHeap h = new BinaryHeap(10);

        h.insert(5);
        h.insert(1);
        h.insert(98);
        h.insert(44);

    }


    int PQ[];
    int size;

    BinaryHeap(int maxInt){
        PQ = new int[maxInt];
    }


    void insert(int value)
    {
        PQ[size++] = value;
        //swim(value);

    }

    int delMax()
    {
        int max = PQ[1];
        return max;

    }

    int size(){
        return size;
    }


}
