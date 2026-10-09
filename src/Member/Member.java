package Member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import Library.Book;
import Library.Library;

public abstract class Member {

	private String name;
	private int id;
	protected Scanner sc;
	private Library library;

	// ★★★借りた本（先生も生徒も共通）
	private List<Book> bookList = new ArrayList<>();

	public Member(String name, int id, Scanner sc, Library library) {
		this.name = name;
		this.id = id;
		this.sc = sc;
		this.library = library;
	}

	public String getName() {
		return name;
	}

	public int id() {
		return id;
	}

	public List<Book> getBookList() {
		return Collections.unmodifiableList(bookList);
	}

	//子クラスが決める
	// 「先生」「生徒」
	protected abstract int getMaxBooks();

	protected abstract String getCallName();

	//borrow()は共通のふるまい
	public void borrow() {

		//テンプレートメソッドパターン
		if (bookList.size() >= getMaxBooks()) {
			System.out.println("もう、借りられない");
			return;
		}

		System.out.println(getCallName() + ",何を借りますか?");
		System.out.println("借りられるのは、" + getMaxBooks() + "冊まで");
		System.out.println("借りたい本の、IDを入力してください");

		List<Book> libraryBooks = library.getBookList();

		for (Book book : libraryBooks) {
			if (book.getBorrowed()) {
				System.out.println("貸出中");
			} else {
				System.out.println(book.getId() + ":" + book.getName());
			}
		}

		while (!sc.hasNextInt()) {
			System.out.println("数値を入力してください");
			sc.next();
		}

		int index = sc.nextInt();

		for (Book book : libraryBooks) {
			if (book.getId() == index) {
				if (book.getBorrowed()) {
					System.out.println("その本は、すでに借りられています"
							+ "やりなおしてください");
					break;
				}

				System.out.println(book.getName() + "を借りました");
				book.setBorrowed();
				bookList.add(book);//本を貯める
				System.out.println(book.getName() + "を借りました");

				return;
			}

		}

		System.out.println("借りられませんでした");

	}

	//returnも共通のふるまい
	public void returnBook() {

		if (bookList.isEmpty()) {
			System.out.println("借りている、本はありません");
			return;
		}

		System.out.println("あなたが、借りている本です");
		System.out.println("IDを入力してください");
		for (Book book : bookList) {
			System.out.println(book.getId() + ":" + book.getName());
		}

		int indexId;
		while (true) {

			if (!sc.hasNextInt()) {
				System.out.println("数値を入力してください");
				sc.next();
				continue;
			}
			indexId = sc.nextInt();
			break;
		}

		Book returnBook = null;
		for (Book book : bookList) {
			if (book.getId() == indexId) {
				returnBook = book;
				break;
			}
		}
		
		System.out.println(returnBook.getName()+"を返却します");
		returnBook.setReturn();//返却する
		
		//抽象メソッドにしなくても、返却はただ減らすだけなので、removeする
		bookList.remove(returnBook);
	}

}
