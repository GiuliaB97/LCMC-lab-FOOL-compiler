package compiler;

import compiler.FOOLParser.TypeContext;
import compiler.lib.Node;


public class ParNode extends Node{
	String id;   
	//Node type;
	TypeContext type;
	
	
	//getter e setter per i campi
	public void setId(String id) { this.id=id; }
	public String getId() { return id; }
	
	/*Mi sembravano corretti ma se non metto TypeContext mi da un errore sul tipo
	public void setType(Node type) { this.type=type; }
	public Node getType() { return type; }
	*/
	
	public void setType(TypeContext type) {this.type=type;}
	public TypeContext getType() { return type; }
}
