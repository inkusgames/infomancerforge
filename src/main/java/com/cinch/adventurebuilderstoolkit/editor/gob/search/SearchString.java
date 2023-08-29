package com.cinch.adventurebuilderstoolkit.editor.gob.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import com.cinch.adventurebuilderstoolkit.beans.gobs.GOBInstance;

public class SearchString {

	Pattern terms=Pattern.compile("[\\|\\&()]");
	Pattern operations=Pattern.compile("[a-zA_Z0-9 ]*");

	List<Term> allTerms=new ArrayList<>();
	List<Operation> allOperations=new ArrayList<>();
	
//	private String searchString;
	
	public SearchString(String searchString) {
//		this.searchString=searchString;
		
		StringBuffer sb=new StringBuffer();
		for (int p=0;p<searchString.length();p++) {
			char c=searchString.charAt(p);
			boolean operationAdded=true;
			if ('('==c) {
				allOperations.add(new OperationOpen());
			} else if (')'==c){
				allOperations.add(new OperationClose());
			} else if ('|'==c){
				allOperations.add(new OperationOr());
			} else if ('&'==c){
				allOperations.add(new OperationAnd());
			} else {
				operationAdded=false;
				sb.append(c);
			}
			if (operationAdded) {
				String term=sb.toString().trim();
				if (term.length()>0) {
					allTerms.add(new TermTest(term));
				}
				sb.setLength(0);
				System.out.println("Operation:"+c);
			}
		}
		String term=sb.toString().trim();
		if (term.length()>0) {
			allTerms.add(new TermTest(term));
		}
		
		
//		
//		for (var s:terms.split(searchString)) {
//			if (s.trim().length()>0) {
//				allTerms.add(new TermTest(s.trim()));
//				System.out.println("Term[]="+s.trim());
//			}
//		}
//		
//		for (var s:operations.split(searchString)) {
//			if ("(".equals(s)) {
//				allOperations.add(new OperationOpen());
//			}else if (")".equals(s)) {
//				allOperations.add(new OperationClose());
//			}else if ("&".equals(s)) {
//				allOperations.add(new OperationAnd());
//			}else if ("|".equals(s)) {
//				allOperations.add(new OperationOr());
//			}
//			System.out.println("Operation[]="+s.trim());
//		}		
	}

	public boolean testSearch(GOBInstance gobInstance) {
		Stack<Term> terms=new Stack<>();
		Stack<Operation> operations=new Stack<>();
		
		terms.addAll(allTerms);
		operations.addAll(allOperations);
		System.out.println("Search Start");
		
		while (operations.size()>0) {
			operations.pop().executeOperation(gobInstance,terms,operations);
		}
		
		return terms.pop().testSearch(gobInstance);
//		return true;
	}
	
	
	private interface Term {
		public boolean testSearch(GOBInstance gobInstance);
	}
	
	private class TermTest implements Term {
		private String test;
		
		public TermTest(String test) {
			this.test=test;
			System.out.println("Term:"+test);
		}
		
		public boolean testSearch(GOBInstance gobInstance) {
			System.out.println("Search:"+test+"="+gobInstance.testSearch(test));
			return gobInstance.testSearch(test);
		}
	}
	
	private class TermValue implements Term {
		private boolean value;
		
		public TermValue(boolean value) {
			this.value=value;
		}
		
		public boolean testSearch(GOBInstance gobInstance) {
			return value;
		}
	}
	
	private abstract class Operation {
		
		public void executeOperation(GOBInstance gobInstance,Stack<Term> terms,Stack<Operation> operations) {
			if (operations.size()>0 && operations.peek().weight()>weight()) {
				var saveTerm=terms.pop();
				operations.pop().executeOperation(gobInstance, terms, operations);
				terms.push(saveTerm);
			}
			execute(gobInstance,terms,operations);
		}
		
		abstract public void execute(GOBInstance gobInstance,Stack<Term> terms,Stack<Operation> operations);
		abstract public int weight();
	}

	private class OperationOr extends Operation {
		public int weight() {
			return 10;
		}
		
		public void execute(GOBInstance gobInstance,Stack<Term> terms,Stack<Operation> operations) {
			System.out.println("Or:");
			boolean b1=terms.pop().testSearch(gobInstance);
			boolean b2=terms.pop().testSearch(gobInstance);
			terms.push(new TermValue(b1 || b2));
		}
	}

	private class OperationAnd extends Operation {
		
		public int weight() {
			return 20;
		}
		
		public void execute(GOBInstance gobInstance,Stack<Term> terms,Stack<Operation> operations) {
			System.out.println("And:");
			boolean b1=terms.pop().testSearch(gobInstance);
			boolean b2=terms.pop().testSearch(gobInstance);
			terms.push(new TermValue(b1 && b2));
		}
	}

	private class OperationOpen extends Operation {
		public int weight() {
			return 0;
		}
		
		public void execute(GOBInstance gobInstance,Stack<Term> terms,Stack<Operation> operations) {
		}
	}

	private class OperationClose extends Operation {
		public int weight() {
			return 50;
		}
		
		public void execute(GOBInstance gobInstance,Stack<Term> terms,Stack<Operation> operations) {
			while (operations.size()>0) {
				var operation=operations.pop();
				
				if (operation instanceof OperationOpen) {
					break;
				} else {
					operation.execute(gobInstance,terms,operations);
				}
			}
		}
	}
}
