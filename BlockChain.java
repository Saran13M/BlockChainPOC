// Block Chain should maintain only limited block nodes to satisfy the functions
// You should not have all the blocks added to the block chain in memory 
// as it would cause a memory overflow.

import java.util.*;


public class BlockChain {
    public static final int CUT_OFF_AGE = 10;
	private Node root; //tree for the block chain data
	private Node maxHeightNode = null;
	private TransactionPool transactionPool = null ;
	
	private class BlockData{
		Block b;
		UTXOPool upool;
		int blockHeight = 0;
	}
	
	private UTXOPool addCoinBaseToUTXOPool(Transaction coinBase, UTXOPool upool)
	{
		byte[]txHash = coinBase.getHash();
		ArrayList<Transaction.Output>outputs =  coinBase.getOutputs() ;
		for(int k=0;k< outputs.size();k++)
		{
			Transaction.Output op = outputs.get(k);
			upool.addUTXO(new UTXO(txHash,k),op);
		}
		return upool;
	}

    /**
     * create an empty block chain with just a genesis block. Assume {@code genesisBlock} is a valid
     * block
     */
    public BlockChain(Block genesisBlock) {
        // IMPLEMENT THIS
		transactionPool = new TransactionPool();
		//create a UTXOPool by adding all the transactions in the UTXOPool
		Transaction coinbase = genesisBlock.getCoinbase();
		byte[]txHash = coinbase.getHash();
		ArrayList<Transaction.Output>outputs =  coinbase.getOutputs() ;
		UTXOPool utxopool = new UTXOPool();
		for(int k=0;k< outputs.size();k++)
		{
			Transaction.Output op = outputs.get(k);
			utxopool.addUTXO(new UTXO(txHash,k),op);
		}
		BlockData blockData	= new BlockData();
		blockData.b = genesisBlock;
		blockData.upool = new UTXOPool(utxopool);
		blockData.blockHeight = 0;
			
		root = new Node(blockData,null);
		maxHeightNode= root;
    }

    /** Get the maximum height block */
    public Block getMaxHeightBlock() {
        
		return maxHeightNode.data.b;
    }

    /** Get the UTXOPool for mining a new block on top of max height block */
    public UTXOPool getMaxHeightUTXOPool() {
        // IMPLEMENT THIS
		return maxHeightNode.data.upool;
    }

    /** Get the transaction pool to mine a new block */
    public TransactionPool getTransactionPool() {
        // IMPLEMENT THIS
		return transactionPool;
    }

    /**
     * Add {@code block} to the block chain if it is valid. For validity, all transactions should be
     * valid and block should be at {@code height > (maxHeight - CUT_OFF_AGE)}.
     * 
     * <p>
     * For example, you can try creating a new block over the genesis block (block height 2) if the
     * block chain height is {@code <=
     * CUT_OFF_AGE + 1}. As soon as {@code height > CUT_OFF_AGE + 1}, you cannot create a new block
     * at height 2.
     * 
     * @return true if block is successfully added
     */
    public boolean addBlock(Block block) {
        // IMPLEMENT THIS
		//get the parent of this block.
		Node node = getParentNode(block);
		if(!canNodeBeAdded(node))
			return false;
		UTXOPool upool = node.data.upool;
		upool = addCoinBaseToUTXOPool(block.getCoinbase(),upool);
		//get the utxo pool related to that block.
		TxHandler txhandler = new TxHandler(upool);
		Transaction[] txs = new Transaction[block.getTransactions().size()];
		block.getTransactions().toArray(txs);
		
		//pass the utxopool to txhandler.
		//pass the transactions of this block.
		Transaction[]acceptedTxs =   txhandler.handleTxs(txs);
		//remove the accepted ones from the transaction pool
		for(Transaction tx:acceptedTxs)
		{
			transactionPool.removeTransaction(tx.getHash());
		}
		//get the utxo pool.
		UTXOPool newUpool = txhandler.getUTXOPool();
		//create a new node 
		BlockData blockData = new BlockData();
		blockData.b = block;
		blockData.upool = newUpool;
		blockData.blockHeight = node.data.blockHeight +1;
		Node nodeToBeAdded = new Node(blockData,node);
		//check if this can be the heighest node
		if(maxHeightNode.data.blockHeight < blockData.blockHeight)
		{
			maxHeightNode = nodeToBeAdded;
		}
		node.addChild(nodeToBeAdded);
		
		
		return true;
    }
	
	private boolean canNodeBeAdded(Node node)
	{
		if(node ==  null)
			return false;
		if((node.data.blockHeight +1) <CUT_OFF_AGE)
		{
			return true;
		}
		return false;
	}
	
	//gets the parent of this block based on the previous hash
	private Node getParentNode(Block b)
	{
		//get the previous hash of this block.
		byte[] prevBlockHash = b.getPrevBlockHash();
		//loop through the blockchain tree to search for this hash.
		//start with root
		Node nodeFound = root.getMatchingNode(prevBlockHash);
		return nodeFound;		
		
	}
	
	

    /** Add a transaction to the transaction pool */
    public void addTransaction(Transaction  tx){
        transactionPool.addTransaction(tx);
    }
	
	
	
	private class Node {
    BlockData data;
    Node parent;
    ArrayList<Node> children;
		
	public Node(BlockData dataToAdd,Node parent)
	{
		children = new ArrayList<Node>();
		this.data = dataToAdd;
		this.parent = parent;
	}
	
	private boolean compareHashes(byte[]a,byte[]b)
	{
		if(a == null || b == null)
			return false;
		ByteArrayWrapper ab = new ByteArrayWrapper(a);
		ByteArrayWrapper bb = new ByteArrayWrapper(b);
		return ab.equals(bb);
	}
	
	public void setData(BlockData dataToAdd)
	{
		this.data = dataToAdd;
	}
	
	public BlockData getData()
	{
		return data;
	}
	
	//given a hash find the node that has the block with this hash.
	public Node getMatchingNode(byte[] blockHash)
	{
		//check if it matches my hash, if yes return this, else loop through children to find a matches
		if(compareHashes(this.data.b.getHash(),blockHash))
		{
			return this;
		}
		else
		{
			for(Node child: children)
			{
				//match found
				if(child.getMatchingNode(blockHash) != null)
				{
					return child;
				}
			}
			return null;
		}
			
	}
	/*
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
	*/
	public void addChild(Node  child)
	{
		if(child != null)
		{
			children.add(child);
		}			
	}		
	/*
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
	*/
}
	

}