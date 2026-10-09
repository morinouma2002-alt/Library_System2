package Member;

import java.util.Scanner;

import Library.Library;

public class Teacher extends Member {

	public Teacher(String name, int id, Scanner sc, Library library) {
		super(name, id, sc, library);
	}

	@Override
	protected int getMaxBooks() {
		return 5;
	}

	@Override
	protected String getCallName() {
		return "先生";
	}
}