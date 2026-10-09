package Library;

public class Book {

	private String name;
	private String direc;
	private int id;
	
	private boolean borrowed=false;

	public Book(String name, String direc, int id) {
		this.name = name;
		this.direc = direc;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}
	
	public boolean getBorrowed() {
		return borrowed;
	}
	
	public void setBorrowed() {
		borrowed=true;
	}
	
	public void setReturn() {
		borrowed=false;
	}
}
