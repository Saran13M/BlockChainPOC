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
		for(Node<T> child:children)
		{
			int childHeight = child.getHeight();
			if(maxHeight < childHeight)
			{
				maxHeight = childHeight;
			}
		}
		return maxHeight;
	}
	
	public void addChild(Node <T> child)
	{
		if(child)
		{
			children.add(child);
		}			
	}		
	
	public static void main(void)
	{
		Node<int> root = new Node(5);
		root.addChild(new Node(10));
		
		System.out.println("Height :"+root.getHeight());
	}
	
}