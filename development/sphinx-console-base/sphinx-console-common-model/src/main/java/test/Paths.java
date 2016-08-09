package test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SKuptsov on 25.09.2015.
 */
public class Paths {

    boolean[] marked;
    int s;
    Graph g;
    boolean hasPathTo=false;
    List<Integer> path = new ArrayList<Integer>();


    Paths(Graph G, int s){
        marked = new boolean[G.V];
        this.s = s;
        this.g = G;

    }


    void dfs(int v){

        marked[v] = true;
        System.out.println(marked[v] + " "+v);
        for(Integer v1 : g.adj(v))
            if(!marked[v1]) dfs(v1);
    }

    boolean hasPathTo(int v){

        marked[v] = true;
        if (v == s) hasPathTo = true;
        for(Integer v1 : g.adj(v)) {
            if (!marked[v1]) hasPathTo(v1);
        }

        return hasPathTo;
    }



    Iterable<Integer> pathTo(int v){

        marked[v] = true;
        if (v != s) path.add(v);
        for(Integer v1 : g.adj(v)) {
            if (!marked[v1]) pathTo(v1);
        }

        return path;
    }

    public static void main(String[] args) {


        Graph g = new Graph(6);
        g.addEdge(0,1);
        g.addEdge(0,2);
        g.addEdge(0,5);
        g.addEdge(3,5);
        g.addEdge(3,4);
        g.addEdge(2,3);
        g.addEdge(2, 4);
        g.addEdge(1, 2);

        Paths paths = new Paths(g, 0);
        //System.out.println(paths.hasPathTo(1));

        for(Integer f : paths.pathTo(3))
            System.out.print(f+" ");




    }

}
