import java.io.*;

/**
 * A class that implements a min heap data structure using
 * an array of strings.
 */
public class minHeap {
    private String[] heap;
    private int numNodes;

    /**
     * Takes in an array of strings and convets the array into a
     * min heap.
     * @param input The array of strings to turn into a min heap
     * @param numNodes The number of nodes (strings) in the array
     * @return A reference to the newly created heap
     */
    public String[] createHeap(String[] input, int numNodes) {
        this.numNodes = numNodes;
        int numParents = numNodes / 2;
        for(int i = numNodes; i > 0; i--) {
            //downheap each node, starting at the bottom, until it's sorted
            downHeap(input, i - 1, numNodes);
        }
        this.heap = input;
        return input;
    }

    /**
     * Downheaps the given parent node by comparing it to it's children
     * and moving it further down the tree if it's comes after
     * it's children, this goes on until no more swaps are made.
     * @param input The array of strings to downheap
     * @param parent The parent node to downheap
     * @param numNodes The number of nodes in the array
     */
    private void downHeap(String[] input, int parent, int numNodes) {
        boolean swapMade = true;
        int smallest = parent;
        while(swapMade) {
            swapMade = false;
            int leftChild = (smallest * 2) + 1;
            int rightChild = (smallest * 2) + 2;

            if(leftChild <= numNodes - 1 &&
               input[smallest].compareTo(input[leftChild]) > 0) {
                smallest = leftChild;
                swapMade = true;
            }

            if(rightChild <= numNodes - 1 &&
               input[smallest].compareTo(input[rightChild]) > 0) {
                smallest = rightChild;
                swapMade = true;
            }

            if(smallest != parent) {
                //swap the smallest node with it's parent
                String temp = input[smallest];
                input[smallest] = input[parent];
                input[parent] = temp;
                parent = smallest;
            }
        }
    }

    /**
     * Returns the string at the top of the heap and
     * places the next appropriate string at the top.
     * @return The string at the top of the heap
     */
    public String pop() {
        if(numNodes == 0) {
            return null;
        }
        String top = this.heap[0];
        this.heap[0] = this.heap[this.numNodes - 1];
        this.heap[this.numNodes - 1] = null;
        this.numNodes--;
        downHeap(this.heap, 0, this.numNodes);
        return top;
    }
}