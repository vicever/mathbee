package com.roger.mathbee;

public enum Operator {
	ADDITION ("\u002B", 0),
	SUBTRACTION ("\u2212", 1),
	MULTIPLICATION ("\u00D7", 2),
	DIVISION ("\u00F7", 3);

	private final String symbol;
	private final int operation;
	
	private Operator(String symbol, int operation) {
		this.symbol = symbol;
		this.operation = operation;
	}
	public String symbol() {
		return symbol;
	}
	public int operation() {
		return operation;
	}
	
	public int calculate(int a, int b) {
		switch (this) {
		case MULTIPLICATION:	return a*b;
		case DIVISION:			return (int) a/b;
		case ADDITION:			return a+b;
		case SUBTRACTION:		return a-b;
		}
		return 0;
	}
	
	public static Operator getOperator(int operation) {
		switch(operation) {
		case 0:	return Operator.ADDITION;
		case 1: return Operator.SUBTRACTION;
		case 2: return Operator.MULTIPLICATION;
		case 3: return Operator.DIVISION;
		default:	return null;
		}

	}
	
}

