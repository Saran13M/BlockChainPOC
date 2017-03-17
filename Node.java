import java.util.*;

public class Node<T> {
    private T data;
    private Node<T> parent;
    private List<Node<T>> children;
	
	public Node(T dataToAdd)
	{
		children = new ArrayList<Node<T>>();
		this.data = dataToAdd;
	}
	
	public void setData(T dataToAdd)
	{
		this.data = dataToAdd;
	}
	
	public T getData()
	{
		return data;
	}
	
	public int getHeight()
	{
		//call get height for each of the nodes in the linked list 
		int maxHeight = 0;
		int myHeight = 0;
		
		if(children.size()>0)
		{
			myHeight = 1;
		}
		
		for(Node<T> child:children)
		{
			int childHeight = child.getHeight();
			if(maxHeight < childHeight)
			{
				maxHeight = childHeight;
			}
		}
		
		
		return maxHeight + myHeight ;
	}
	
	public void addChild(Node <T> child)
	{
		if(child != null)
		{
			children.add(child);
		}			
	}		
	
	public static void main(String args[])
	{
		Node<String> root = new Node("5");
		Node<String>secondNode = new Node("dd");
	
		Node<String>testNode = new Node("dfd");
		testNode.addChild(new Node("sdsf"));
		secondNode.addChild(testNode);
		secondNode.addChild(new Node(" "));
		
		root.addChild(secondNode);
		
		
		System.out.println("Height :"+root.getHeight());
	}
	
}