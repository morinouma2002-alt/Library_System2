package Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Library.Book;
import Library.Library;

public class Student extends Member {
	
	
	private List<Book> studentBookList=new ArrayList<>();


	public Student(String name, int id,Scanner sc,Library library) {

		super(name, id,sc,library);
		
	}
	
	@Override 
	protected int getMaxBooks() {
		return 3;
	}
	
	@Override
	protected String getCallName() {
		return "生徒";
	}
	
	
}
