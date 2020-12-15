package me.noobedidoob.minigames.utils;

public class Pair {
	
	private Object o1;
	private Object o2;
	
	public Pair(Object o1, Object o2) {
		super();
		this.o1 = o1;
		this.o2 = o2;
	}

	public Object get1() {
		return o1;
	}

	public void set1(Object o1) {
		this.o1 = o1;
	}

	public Object get2() {
		return o2;
	}

	public void set2(Object o2) {
		this.o2 = o2;
	}
}
