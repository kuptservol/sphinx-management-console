package test;

import java.util.*;
import java.util.LinkedList;

/**
 * Created by SKuptsov on 25.09.2015.
 */
public class Graph {

    List<Integer>[] adj;
    int V;
    int E;

    public Graph(int V){
        this.V = V;
        adj = new List[V];

        for(int i = 0; i<V; i++)
            adj[i] = new LinkedList();
    }

    public int V(){return V;}
    public int E(){return E;}

    void addEdge(int v, int w)
    {
        adj[v].add(w);
        adj[w].add(v);
        E++;
    }

    Iterable<Integer> adj(int v)
    {
        return adj[v];
    }
}
