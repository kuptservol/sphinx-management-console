package test;

import java.util.Collection;

/**
 * Created by SKuptsov on 23.09.2015.
 */
public class LinkedList<E>  {

    Node<E> first;
    Node<E> last;

    private static class Node<E>{
        Node<E> next;
        Node<E> last;
        E value;
    }


}
