# OOP設計 再採点(15点満点)

## 結論(先に回答)

- **OOP設計: 9.5 / 15**(前回 4/15 は低すぎました)。
- 基準は「クラス・継承・抽象クラス・ArrayList まで習った初学者」。例外/try-catch/テスト/Javadoc、返却・検索の未実装は含めていません。
- 「OOPを意識して作っている」はコードから読み取れます。カプセル化、abstract な親、`Member` 型での呼び出し(多態性の入口)、`Library`/`Manager` の分担は実際にできています。
- 残る課題は主に1つです。**親 `Member` に共通部分を引き上げていない**ことです。あなたが `Student.java` 39行に書いた「★共通のふるまいなので後で継承でわける?」の問題意識のとおりです。

## 前回の指摘の検証

| 前回の主張 | 判定 | 根拠 |
|---|---|---|
| `Member.borrow()` が空実装 | **真** | `Member.java` 20〜22行。`{ }` の中身が空。abstract メソッドではない。前回は「18行付近」としていたが、実際は20行。 |
| Student/Teacher の `borrow` が重複 | **真** | `Student.java` 24〜80行と `Teacher.java` 24〜78行はほぼ同一。差は上限(`Student` 27行 `>= 3` / `Teacher` 26行 `>= 5`)と、リスト名(`studentBookList`/`teacherBookList`)だけ。フィールド `library`/`sc`/本リストも両方に同じ宣言がある(`Student.java` 12〜14行、`Teacher.java` 12〜15行)。 |
| Student/Teacher 内に Scanner と println | **真** | `Student.java` 14行(Scanner フィールド)、28・34〜36・44・46・52・60・65・75行(println)、50・55行(sc 使用)。`Teacher.java` も同様(12行、27・33〜35・41・43・49・58・63・73行など)。 |
| 「多態性の恩恵がない」 | **一部偽** | `SystemMain.java` 27行で `Member member;`、65行で `member.borrow()` と親型で呼んでいる。実行時に Student/Teacher のどちらかが動くので、多態性は**成立している**。ただし、2つの中身がほぼ同じなので、使う意味が薄い。 |
| 「Student/Teacher の差は上限冊数だけ」 | **ほぼ真** | 上記のとおり。ほかは Student のメッセージのコピペ残り(34〜35行が「先生」「５冊まで」のまま。上限は3)。これは OOP ではなく文言のバグ。 |

## 評価内訳

| 観点 | 配点 | 点 | 根拠 |
|---|---|---|---|
| カプセル化 | 3 | 2.5 | 全フィールド `private`(`Book.java` 5〜9行、`Member.java` 5〜6行、`Library.java` 10行、`Manager.java` 12行)。`Library.getBookList`(25行)と `Manager.getMemberList`(16行)は `Collections.unmodifiableList` で返す。`Book` に自由な setter がない。減点: `setBorrowed()` が引数なしで true にするだけ(戻せない)。`Member.id()` が getter 命名でない。 |
| 継承・抽象化 | 4 | 2 | `abstract class Member`(3行)、`extends Member`、`super(name, id)`(`Student.java` 18行)、`@Override` は正しい。減点: 抽象メソッドがなく(`borrow` が空実装)、共通の `library`・`sc`・借りた本リストを親に上げていない。 |
| 多態性 | 3 | 2 | `Member` 型変数で `borrow()` を呼ぶ(`SystemMain.java` 65行)。`getMember` の戻り値も `Member`(82行)。`Manager` も `List<Member>` で Student/Teacher を混在管理。減点: 子の違いが上限だけなので、メソッド上書きで表現する必然性が薄い。 |
| 責務分割 | 3 | 1.5 | `Library`(本)・`Manager`(会員)・`Book`(本の情報)の分け方は妥当。`Library.java` 12〜13行のコメントで「Libraryが会員を持つのは違和感」と自分で判断して分けたのは良い設計判断。減点: `Student`/`Teacher` が入力(Scanner)・表示(println)・貸出処理を全部担当している。 |
| クラス間関係 | 2 | 1.5 | `Library` has-a `Book`(9行にコメントあり)、`Student`/`Teacher` has-a `Book` のリスト(Teacher 13行)、`Manager` has-a `Member`。依存はコンストラクタ渡し(`Main.java` 13行)で、`new` が散らばっていない。減点: `Member` 側が `Library` と `Scanner` に依存している。 |
| **合計** | **15** | **9.5** | |

## できていること(OOP観点)

- 抽象クラス `Member` を作り、先生と生徒を `is-a` で表現している。
- `private` + getter で、外から勝手に変更できない形にしている。
- `unmodifiableList` で、リストの中身が外から追加・削除されるのを防いでいる。
- コンストラクタで依存オブジェクト(`Library`、`Scanner`)を受け取っている。
- `Library`/`Manager`/`Book` に役割を分けている。
- 設計上の疑問をコメントに残している(`Library.java` 12行、`Student.java` 39行)。

## 残っていること(短い改善案、try-catch不要)

1. **共通部分を親に引き上げる**(今やろうとしている「継承の共通化」の本命)。
   ```java
   public abstract class Member {
       private String name;
       private int id;
       private List<Book> books = new ArrayList<>();  // 共通
       public abstract int getMaxBooks();             // 子で上限だけ決める

       public boolean borrow(Book book) {
           if (books.size() >= getMaxBooks()) return false;
           if (book.getBorrowed()) return false;
           book.setBorrowed();
           books.add(book);
           return true;
       }
   }
   // Student: public int getMaxBooks() { return 3; }
   // Teacher: public int getMaxBooks() { return 5; }
   ```
   `boolean` の戻り値で成功/失敗を伝えれば、例外は不要です。
2. **入力と表示を `SystemMain` 側に移す**。`Student`/`Teacher` から `Scanner` と `println` を外し、`SystemMain` が ID を聞いて `member.borrow(book)` の結果で表示を分けます。これで `Student`/`Teacher` のフィールドから `Scanner` と `Library` が消えます。
3. **本を探す処理を `Library` に持たせる**: `Book findById(int id)`(見つからなければ `null`)。
4. **小さな命名の整理**: `id()` を `getId()` に、`setMemberList` を `addMember` に(メモ.java にも書かれている)、`setBorrowed()` を貸出/返却で対にする(返却を作る時に `setBorrowed(boolean)` か `returnBook()`)。
5. **`SystemMain.getMember` の先生/生徒の重複**(84〜128行)は、`new Teacher`/`new Student` の違いだけなので、共通部分を括り出せます。

## 確認できなかったこと

- 実行はしていません(コード読解のみ)。コンパイル可否も今回は再確認していません(前回レビューでは成功と記録)。

## 一言

前回の 4/15 は、「機能がない」ことと「重複がある」ことを OOP の減点に混ぜていた面があります。機能不足は除き、構造だけを見れば **9.5/15(約63%)で、初学者としては良好**です。上の1番を直せば 12〜13/15 になります。
