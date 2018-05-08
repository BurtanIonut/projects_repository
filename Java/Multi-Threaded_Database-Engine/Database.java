import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.Semaphore;

public class Database implements MyDatabase {
	
	HashMap<String, ArrayList<ArrayList<Object>>> hash = new HashMap<String, ArrayList<ArrayList<Object>>>(); //Tabele
	HashMap<String, Semaphore> semHash = new HashMap<String, Semaphore>();  //Semafoare pentru reader/writer
	HashMap<String, Integer> readHash = new HashMap<String, Integer>();  //Contor de readeri
	HashMap<String, Semaphore> tranHash = new HashMap<String, Semaphore>();  //Semafoare pentru tranzactii
	
	int workerThreads;
	
	Database() {
		
	}

	
	Database(int wrkThreads) {
		
		workerThreads = wrkThreads;
	}

	@Override
	public void initDb(int numWorkerThreads) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopDb() {
		// TODO Auto-generated method stub
		

	}

	@Override
	public void createTable(String tableName, String[] columnNames, String[] columnTypes) {
		
		// TODO Auto-generated method stub
		
		ArrayList<ArrayList<Object>> table = new ArrayList<ArrayList<Object>>();
		ArrayList<Object> entryName = new ArrayList<Object>();
		ArrayList<Object> entryType = new ArrayList<Object>();
		for(int i = 0; i < columnNames.length; i += 1) {
			entryName.add(columnNames[i]);
			entryType.add(columnTypes[i]);
		}
		table.add(entryName);
		table.add(entryType);
		hash.put(tableName, table);
		semHash.put(tableName, new Semaphore(1));
		readHash.put(tableName, 0);
		tranHash.put(tableName, new Semaphore(1));
	}

	@Override
	public ArrayList<ArrayList<Object>> select(String tableName, String[] operations, String condition) {
		
		synchronized (hash.get(tableName)) {
			readHash.put(tableName, readHash.get(tableName) + 1);
			if (readHash.get(tableName) == 1) {
				try {
					semHash.get(tableName).acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		 ArrayList<ArrayList<Object>> res = new  ArrayList<ArrayList<Object>>();
		 
		 if(condition.compareTo("") == 0) {
			 for(int i = 0; i < operations.length; i++) {
				 res.add(computeOperation(hash.get(tableName), operations[i]));
			 }
		 }
		 else {
			 String[] tok = condition.split("\\s");
			 ArrayList<Object> names = hash.get(tableName).get(0);
			 
			 int nrCol = names.indexOf(tok[0]);
			 String type = hash.get(tableName).get(1).get(nrCol).toString();
			 
			 ArrayList<ArrayList<Object>> auxTable = new ArrayList<ArrayList<Object>>();
			 auxTable.add(hash.get(tableName).get(0));
			 auxTable.add(hash.get(tableName).get(1));
			
			 for(int i = 2; i < hash.get(tableName).size(); i ++) {
				 if(checkTruth(tok[1], tok[2], hash.get(tableName).get(i).get(nrCol), type)) {
					 auxTable.add(hash.get(tableName).get(i));
				 }
			 }
			 
			 for(int i = 0; i < operations.length; i++) {
				res.add(computeOperation(auxTable, operations[i]));
			 }
			 
		 }
		 
		 synchronized (hash.get(tableName)) {
				readHash.put(tableName, readHash.get(tableName) - 1);
				if (readHash.get(tableName) == 0) {
						semHash.get(tableName).release();
				}
				
			}
		
		return res;
	}
	
	ArrayList<Object> computeOperation(ArrayList<ArrayList<Object>> table, String op) {
		
		ArrayList<String> tok = new ArrayList<String>();
		ArrayList<Object> res = new ArrayList<Object>();
		StringTokenizer st = new StringTokenizer(op, "()");
		
		while(st.hasMoreTokens()){
			tok.add(st.nextToken());
		}
		
		if(tok.size() == 1) {
			int ind = table.get(0).indexOf(tok.get(0));
			for(int i = 2; i < table.size(); i++) {
				res.add(table.get(i).get(ind));
			}
		}
		else if(tok.size() == 2) {
			return computeOp(tok.get(0), table, table.get(0).indexOf(tok.get(1)));
		}
		
		return res;
		
	}
	
	ArrayList<Object> computeOp(String op, ArrayList<ArrayList<Object>> table, int colNr) {
		
		ArrayList<Object> res = new ArrayList<Object>();
		
		switch(op) {
		
			case "max":
				int max = Integer.parseInt(table.get(2).get(colNr).toString());
				for(int i = 2; i < table.size(); i ++) {
					if (max < Integer.parseInt(table.get(i).get(colNr).toString())) {
						max = Integer.parseInt(table.get(i).get(colNr).toString());
					}
				}
				res.add(max);
				return res;
				
			case "min":
				int min = Integer.parseInt(table.get(2).get(colNr).toString());
				for(int i = 2; i < table.size(); i ++) {
					if (min > Integer.parseInt(table.get(i).get(colNr).toString())) {
						min = Integer.parseInt(table.get(i).get(colNr).toString());
					}
				}
				res.add(min);
				return res;
				
			case "avg":
				int j;
				int avg = 0;
				for(j = 2; j < table.size(); j ++) {
					avg += Integer.parseInt(table.get(j).get(colNr).toString());
				}
				avg = avg/(j - 2);
				res.add(avg);
				return res;
				
			case "sum":
				int sum = 0;
				for(int i = 2; i < table.size(); i ++) {
					sum += Integer.parseInt(table.get(i).get(colNr).toString());
				}
				res.add(sum);
				return res;
				
			case "count":
				res.add(table.size()-2);
				return res;
	
			default:
				break;
		}
		
		return res;
	}
	
	ArrayList<Object> computeResult(String tableName, String op, ArrayList<Object> constrainedCol, String colName) {
		
		return constrainedCol;
	}
	
	@Override
	public void update(String tableName, ArrayList<Object> values, String condition) {

		try {
			semHash.get(tableName).acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 if (condition.compareTo("") == 0) {
			 for(int i = 2; i < hash.get(tableName).size(); i ++) {
					 hash.get(tableName).set(i, values);
			 }
		 }
		 else {
			 String[] result = condition.split("\\s");
			 ArrayList<Object> names = hash.get(tableName).get(0);
			 
			 int nrCol = names.indexOf(result[0]);
			 String type = hash.get(tableName).get(1).get(nrCol).toString();
			 for(int i = 2; i < hash.get(tableName).size(); i ++) {
				 if(checkTruth(result[1], result[2], hash.get(tableName).get(i).get(nrCol), type)) {
					 hash.get(tableName).set(i, values);
				 }
			 }

		 }
		 
		 semHash.get(tableName).release();
		 
	}

	@Override
	public void insert(String tableName, ArrayList<Object> values) {
		
		try {
			semHash.get(tableName).acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		(hash.get(tableName)).add(values);
		
		semHash.get(tableName).release();
	}

	@Override
	public void startTransaction(String tableName) {
		// TODO Auto-generated method stub
		try {
			tranHash.get(tableName).acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		synchronized (hash.get(tableName)) {
			try {
				hash.get(tableName).wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void endTransaction(String tableName) {
		// TODO Auto-generated method stub
		tranHash.get(tableName).release();

	}
	
	boolean checkTruth (String op, String x, Object y, String type) {
		switch(type) {
		
			case "int":
				return checkInt(op, x, y);
			case "string":
				return checkString(op, x, y);
			case "bool":
				return checkBool(op, x, y);
			default:
				break;
		}
		return false;
	}


	private boolean checkBool(String op, String x, Object y) {
		
		switch(op){
		
			case "==":
				if (x.compareTo("true") == 0) {
					if ((y.toString()).compareTo("true") == 0) {
						return true;
					}
					else {
						return false;
					}
				}
				else if (x.compareTo("false") == 0) {
					if ((y.toString()).compareTo("false") == 0) {
						return true;
					}
					else {
						return false;
					}
				}
			default:
				break;
		}
		
		return false;
	}


	private boolean checkString(String op, String x, Object y) {
		
		switch(op){
		
			case "==":
				if(x.compareTo(y.toString()) == 0) {
					return true;
				}
				else {
					return false;
				}
			default:
				break;
		
		}
		return false;
	}


	private boolean checkInt(String op, String x, Object y) {
		
		switch(op){
		
			case "==":
				if(Integer.parseInt(x) == Integer.parseInt(y.toString())) {
					return true;
				}
				else {
					return false;
				}
			case ">":
				if(Integer.parseInt(x) < Integer.parseInt(y.toString())) {
					return true;
				}
				else {
					return false;
				}
			case "<":
				if(Integer.parseInt(x) > Integer.parseInt(y.toString())) {
					return true;
				}
				else {
					return false;
				}
			default:
				break;
		}
		return false;
	}

}
