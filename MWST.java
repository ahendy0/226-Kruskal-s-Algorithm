/* MWST.java
   CSC 226 - Fall 2015
   Assignment 1 - Minimum Weight Spanning Tree Template
   
   This template includes some testing code to help verify the implementation.
   To interactively provide test inputs, run the program with
	java MWST
	
   To conveniently test the algorithm with a large input, create a text file
   containing one or more test graphs (in the format described below) and run
   the program with
	java MWST file.txt
   where file.txt is replaced by the name of the text file.
   
   The input consists of a series of graphs in the following format:
   
    <number of vertices>
	<adjacency matrix row 1>
	...
	<adjacency matrix row n>
	
   Entry A[i][j] of the adjacency matrix gives the weight of the edge from 
   vertex i to vertex j (if A[i][j] is 0, then the edge does not exist).
   Note that since the graph is undirected, it is assumed that A[i][j]
   is always equal to A[j][i].
	
   An input file can contain an unlimited number of graphs; each will be 
   processed separately.


   B. Bird - 08/02/2014
*/

import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;
import java.io.File;

//Do not change the name of the MWST class
public class MWST{

	/* mwst(G)
		Given an adjacency matrix for graph G, return the total weight
		of all edges in a minimum weight spanning tree.
		
		If G[i][j] == 0, there is no edge between vertex i and vertex j
		If G[i][j] > 0, there is an edge between vertices i and j, and the
		value of G[i][j] gives the weight of the edge.
		No entries of G will be negative.
	*/
	
	static int MWST(int[][] G){
		int numVerts = G.length;
		ListNode toSort = new ListNode(new Edge(0,0,0,new Vert[numVerts]) );
		ListNode p = toSort;;
		int totalWeight = 0;
		Vert[] ref = new Vert[numVerts];
		
		for(int i = 0;i<numVerts;i++){
			ref[i] = new Vert(i);

		}

		int count2 = 0; //save pos for diagonal traversal
		for(int row = 0; row <numVerts;row++){
			count2++;		//each time we travel down a row, begin column by 1
			for(int column=count2; column< numVerts;column++){
				
				if(G[row][column]>0){
					p.next = new ListNode(new Edge(row,column, G[row][column],ref));		//push to ListNode
					p = p.next;
					
				}
			}
		}
		
		
	 	toSort = removeFront(toSort);	//removes extra object on listnode, was unpreventable
		
		ListNode sorted = MergeSort(toSort); //sort 
		
		//printList(sorted);
		//sorted now contains a  sorted ListNode of edge objects.
		ListNode sorted2 = sorted;
	
		while(sorted!= null){ //traverse through sorted nodes
			Edge a = sorted.value;
			int leastEdge = a.weight;
			sorted = sorted.next;
			totalWeight+= union(findSet(a.x),findSet(a.y),leastEdge); 
		}
		return totalWeight;

	}

	public static Vert findSet(Vert a){
		if(a.parent != a){
			a.parent = findSet(a.parent); //sets parent to whatever highest parent on tree is
		}								// uses tree compression for faster speed 

		return a.parent;


	}
	
	public static int union(Vert a, Vert b, int leastEdge){
		
		//System.out.println("(a.val,a.parent.val), ("+a.val+", "+ a.parent.val+ ")....(b.val,b.parent.val), ("+ b.val +","+b.parent.val+")\n");

		if(a==b){ //case that a cycle will exist, unnessecary to have cycletest branch
			return 0; //totalweight += 0
		}
		else if(a.rank>b.rank){						//use Union
			b.parent = a;
			
		} else if (a.rank<b.rank){
			a.parent = b;

		}else if (a.rank == b.rank){
			a.parent = b;
			b.rank ++;
		}
		
		return leastEdge;

		
	}

	public static ListNode removeFront(ListNode head){
		ListNode f = head;
		head = head.next;
		f = null;
		return head;


	}

	public static int listLength(ListNode node){
		if (node == null)
			return 0;
		return 1 + listLength(node.next);
	}

	public static ListNode addBack(ListNode head, ListNode value){
		ListNode sorted;
		if(value == null){
			return head;
		}
		if(head == null){
			return value;

		}
		if ( head.value.weight< value.value.weight){
			sorted = new ListNode( head.value, addBack(head.next,value));
		}
		else{
			sorted = new ListNode( value.value, addBack(head,value.next));
		}
		return sorted;

	}

	public static ListNode findMid(ListNode head,ListNode end){
		ListNode mid = head;

		if (end == null||end.next == null||end.next.next==null){ //end.next.next is required or else null pointers would happen on a few cases.
			return mid;														// also size n  = 4, middle pointer would be too far.
		}
		end = end.next;
		return findMid(head.next,end.next);			// recursively increment back pointer by 2, front by one making front pointer be at middle.

	}

	public static ListNode MergeSort(ListNode head){
		if(listLength(head)<=1 || head ==null ){
			return head;
		}
		ListNode middle = findMid(head,head); 
		ListNode tail = middle.next; //break from middle pointer.next to make tail		
		middle.next = null; //middle pointer edits head linked list, breaking it into head nodes
		
		ListNode left =  MergeSort(head); //recursively create lists of size one
		ListNode right = MergeSort(tail); //by continuously splitting in half by findMid

		return addBack(left,right);   //originally was listMerge, edited my add to back function to create a sort and merge function
	}
	
	public static void printList(ListNode node){
		if (node == null)
			System.out.println();
		else{
			System.out.print(node.value.weight + " ");
			printList(node.next);
		}
	}

	public static class ListNode{
		Edge value;
		ListNode next;
		public ListNode(){
			
			next = null;
		}
		public ListNode(Edge value){
			this.value = value;
			this.next = null;
		}
		public ListNode(Edge value, ListNode next){
			this.value = value;
			this.next = next;
		}
	}

	public static class Edge{
		public Vert x;
		public Vert y;
		public int weight;
		//public boolean visited = false;
		public Edge(int a,int b,int weight, Vert[] ref){
			this.weight = weight;
			this.x = ref[a];
			this.y = ref[b];

			
		}

	}

	public static class Vert{
		public Vert parent;
		public int val;
		public int rank= 0;
		public Vert(int val){
			this.val = val;
			this.parent = this;
			
		}
		public Vert(int val, Vert parent){
			this.val = val;
			this.parent = parent;


		}

	}
		
	/* main()
	   Contains code to test the MWST function. You may modify the
	   testing code if needed, but nothing in this function will be considered
	   during marking, and the testing process used for marking will not
	   execute any of the code below.
	*/
	public static void main(String[] args){
		Scanner s;
		if (args.length > 0){
			try{
				s = new Scanner(new File(args[0]));
			} catch(java.io.FileNotFoundException e){
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}else{
			s = new Scanner(System.in);
			System.out.printf("Reading input values from stdin.\n");
		}
		
		int graphNum = 0;
		double totalTimeSeconds = 0;
		
		//Read graphs until EOF is encountered (or an error occurs)
		while(true){
			graphNum++;
			if(graphNum != 1 && !s.hasNextInt())
				break;
			System.out.printf("Reading graph %d\n",graphNum);
			int n = s.nextInt();
			int[][] G = new int[n][n];
			int valuesRead = 0;
			for (int i = 0; i < n && s.hasNextInt(); i++){
				for (int j = 0; j < n && s.hasNextInt(); j++){
					G[i][j] = s.nextInt();
					valuesRead++;
				}
			}
			if (valuesRead < n*n){
				System.out.printf("Adjacency matrix for graph %d contains too few values.\n",graphNum);
				break;
			}
			long startTime = System.currentTimeMillis();
			
			int totalWeight = MWST(G);
			long endTime = System.currentTimeMillis();
			totalTimeSeconds += (endTime-startTime)/1000.0;
			
			System.out.printf("Graph %d: Total weight is %d\n",graphNum,totalWeight);
		}
		graphNum--;
		System.out.printf("Processed %d graph%s.\nAverage Time (seconds): %.2f\n",graphNum,(graphNum != 1)?"s":"",(graphNum>0)?totalTimeSeconds/graphNum:0);
	}
}
