import java.util.ArrayList;
import java.util.List;

public class TxHandler {
	
	UTXOPool mUTXOPool;
	UTXOPool mDuplicatePool;
	//ArrayList<UTXO> seenUTXO;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        // IMPLEMENT THIS
		mUTXOPool = new UTXOPool(utxoPool);
		mDuplicatePool = new UTXOPool();
		//seenUTXO = new ArrayList<UTXO>();
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
		boolean retVal = false;
        // IMPLEMENT THIS
		//get all inputs from tx
		ArrayList<Transaction.Input>inputs =  tx.getInputs() ;
		//check if each of the inputs are in current UTXO pool.
		double outputValue = 0;
		double inputsValue =0;
		ArrayList<UTXO> seenUTXO = new ArrayList<UTXO>();
		
		for (int i=0;i< inputs.size();i++)
		{
			Transaction.Input ip = inputs.get(i);
			//create a UTXO from this
			UTXO utxo = new UTXO(ip.prevTxHash,ip.outputIndex);
			//check if it is present in the utxopool
			if(seenUTXO.contains(utxo)) return false;
			seenUTXO.add(utxo);
			
			if (mUTXOPool.contains(utxo) == false)
			{
				return false;
			}
				
			Transaction.Output op = mUTXOPool.getTxOutput(utxo);
			inputsValue += op.value;
			//validate the signatures of the inputs
			byte[] data = tx.getRawDataToSign(i);
			//check if this is valid
			if(Crypto.verifySignature(op.address,data,ip.signature) ==false)
			{
				return false;
			}
			//remove the utxo to avoid duplicates
			//create a utxo pool of the utxos which have already been found and should not be duplicated
			//mUTXOPool.removeUTXO(utxo);
						
		}
		
		ArrayList<Transaction.Output>outputs =  tx.getOutputs() ;
		
		for(int j=0;j< outputs.size();j++)
		{
			Transaction.Output op = outputs.get(j);
			if(op.value < 0.0 )
				return false;
			outputValue += op.value;
		}
		
		//check if all the output values are non zero
		if(outputValue > inputsValue)
		{
			return false;
		}
		
		
		return true;
		// check that all the output values are non negative
		// sum if input is greater than or equal to some of output value
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
		List<Transaction>arrToReturn = new ArrayList<Transaction>();
		//check if the transaction is valid 
		for(int j=0;j<10;j++)
		{
			for(int i=0;i< possibleTxs.length;i++)
			{
				Transaction ts = possibleTxs[i];
				if(isValidTx(ts))
				{
					arrToReturn.add(ts);
					//as the transaction is valid add the output of this transaction to 
						//add the output of this transaction to the UTXO pool
					byte[]txHash = ts.getHash();
					ArrayList<Transaction.Output>outputs =  ts.getOutputs() ;
					for(int k=0;k< outputs.size();k++)
					{
						Transaction.Output op = outputs.get(k);
						mUTXOPool.addUTXO(new UTXO(txHash,k),op);
					}	
					//remove the UTXO that have been consumed
					ArrayList<Transaction.Input>inputs =  ts.getInputs() ;
					for(int a=0;a<inputs.size();a++)
					{
						Transaction.Input ip = inputs.get(a);
						//create a UTXO from this
						UTXO utxo = new UTXO(ip.prevTxHash,ip.outputIndex);
						mUTXOPool.removeUTXO(utxo);
					}

	
				}
							
			}
		}
		Transaction[] arr = arrToReturn.toArray(new Transaction[arrToReturn.size()]);
		return arr;
    }
	
	public UTXOPool getUTXOPool()
	{
		return mUTXOPool;
	}

}
