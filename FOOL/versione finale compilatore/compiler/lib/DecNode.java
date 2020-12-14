package compiler.lib;

public abstract class DecNode extends Node {
	//decnode classe madre di tutte le dichairazione
	//serve perch� dentro c'� il campo type in modo da definire il tipo della dichairazione; protected per poterlo modificare solo in maniera controllata
	protected TypeNode type;
		
	public TypeNode getType() {return type;}

}
