package test;

/**
 * Created by SKuptsov on 23.09.2015.
 */
public class BinaryTree<E> {

    Node<E> root;

    private static class Node<E>{
        E value;
        Node<E> left;
        Node<E> right;
    }
}
