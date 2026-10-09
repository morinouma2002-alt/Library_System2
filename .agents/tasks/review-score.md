# レビュー: 図書館の貸出システム(実務基準での採点)

## 結論(先に回答)

- 実務基準: **約 30 / 100点**
- 初学者の課題としての評価: **約 70 / 100点**

「いい感じに治った」のは事実です。getter の `Collections.unmodifiableList`、`Member` の抽象クラス化、`Library`/`Manager` への責務分割は前進しています。ただし実務では、返却機能が無いこと、Student/Teacher のコピペ重複、UI と業務ロジックの混在、入力エラーでクラッシュすることが大きな減点になります。

## 検証結果

- コンパイル: `javac -encoding UTF-8` を一時ディレクトリへ出力し、**成功(exit 0)**。一時ディレクトリは削除済み。
- 実行: 端末出力が崩れたため、対話実行の結果は**確認できていません**。以下のバグはコード読解によるものです。
- git 履歴: `162e292 first commit` の1件のみ(origin/main と同じ)。
- git 管理対象: `bin/*.class`、`.classpath`、`.project`、`.settings/*`、`src/メモ.java` が含まれています。

## 観点別スコア(実務基準)

| 観点 | 点 | 根拠 |
|---|---|---|
| OOP設計 | 4/15 | `Member` は abstract ですが、`borrow()` が空実装です(`Member.java` 18行付近)。abstract メソッドにすべきです。`Student`/`Teacher` の差は上限冊数(3と5)だけで、`borrow` 全体がコピペです。多態性の恩恵がありません。 |
| 正しさ・境界条件 | 4/20 | 下記バグ一覧を参照。返却機能が無いため、本は一度借りたら二度と戻りません。 |
| エラー処理 | 2/10 | 例外が無く、すべて `println` です。メニューで `sc.nextInt()` に文字を入力すると `InputMismatchException` で落ちます(`SystemMain.java` の `int index = sc.nextInt();`)。ID 入力の `sc.nextInt()` も同様です。 |
| 命名・パッケージ | 3/10 | パッケージ名が大文字始まり(`Library`、`Manager`、`Member`)で、規約は小文字です。`Library.Library` は読みづらい名前です。`id()` は `getId()` にすべきです。`direc` は意味不明で、getter もありません。`setBorrowed()` は引数なしで状態を true にするだけなので、`markBorrowed()` などが適切です。`setMemberList(Member)` は動作が add なので `addMember` にすべきです(メモ.java にも本人の指摘あり)。 |
| 関心の分離 | 2/15 | `Student`/`Teacher` が `Scanner` と `System.out` を直接持ち、一覧表示・入力・貸出処理を1メソッドで行っています。ドメインが UI に依存しており、単体テストできません。 |
| 重複・可読性 | 3/10 | `Student.borrow` と `Teacher.borrow` はほぼ同一です。`SystemMain.getMember` の先生/生徒分岐もほぼ同一です。ラベル付き `break` と、`while(true)` が実は1回しか回らない構造は読みにくいです。`Library` のコンストラクタは1行に複数の `add` が並んでいます。 |
| コメント・Javadoc | 2/5 | Javadoc がありません。`// ★★★` などの TODO 風コメントが残っています。 |
| テスト容易性 | 1/10 | テストが無く、`Scanner` を直接 new/注入する構造でロジックを切り離せません。 |
| ゴミ・リポジトリ衛生 | 1/5 | `メモ.java` は学習メモを `.java` に入れたもので、ソースツリーにあるのは不適切です。`bin/` の .class と Eclipse 設定がコミットされています。`.gitignore` がありません。 |
| **合計** | **22/100 相当 → 動作する分を加味して約30** | |

## バグ・不整合の一覧(根拠付き)

1. **返却・検索が未実装**: `SystemMain.java` の switch に case 2(返却)と case 3(検索)がありません。メニューには表示されます。選ぶと何も起きずメニューに戻ります。
2. **Student の上限メッセージが誤り**: `Student.java` の上限チェックは `>= 3` ですが、案内文は「借りられるのは、５冊まで」で、「先生、何を借りますか?」とも表示されます。Teacher からのコピペ残りです。
3. **誤入力時のメッセージが不正**: 貸出中の本を選ぶと、「すでに借りられています」の後に for を抜けて「借りられませんでした」も表示されます。ID 不一致でも同じ表示です。再入力のループにはならず、1回で終了します。ループが機能していません。
4. **貸出一覧の表示**: 貸出中の本は ID も書名も出さず「貸出中」とだけ表示されるので、どの本か分かりません。
5. **会員の同一性が名前だけ**: `SystemMain.getMember` は名前だけで既存会員を探します。先生「A」が登録されていると、生徒として「A」でログインしても先生が返ります。ID は照合に使われていません。
6. **終了コマンド**: 冒頭に「システム終了は -1」とありますが、会員種別の選択では `-1` が無効入力扱いになります。メニュー中の `-1` だけ有効です。メニューでの無効な数字は何も表示されず再表示されます。
7. **Book の状態**: `setBorrowed()` は true にするだけで、戻す手段がありません。誰が借りているかも持っていません。
8. **未使用要素**: `Book.direc` に getter が無く、使われていません。`Member.id()` も未使用です。`Scanner` は close されません。

## 良い点

- `Collections.unmodifiableList` による防御的コピー的な配慮があります。
- 貸出中の二重貸出は `getBorrowed()` で防げています(このシステム内では)。
- `Main` と `SystemMain` を分けようとしています。
- 依存を DI(コンストラクタ渡し)にしています。
- 自分の疑問(Library が Member を持つ違和感)をコメントに残して設計を考えており、学習姿勢は良好です。

## 改善点 トップ5(優先順)

1. **`borrow` の重複を解消し、ドメインと UI を分離する** (`Student.java`、`Teacher.java`、`Member.java`)。`Member` に `abstract int getMaxBooks()` を持たせ、Student=3、Teacher=5 とします。貸出ロジックは `Member.borrow(Book)` が成功/失敗(例外または結果型)を返すだけにし、`Scanner` と `println` は `SystemMain` 側へ集約します。
2. **返却と検索を実装する** (`SystemMain.java` の switch、`Book.java`)。`Book.returnBook()` を追加し、`Member` に借りた本のリストと `returnBook(int id)` を持たせます。未貸出の本の返却は例外で拒否します。
3. **入力エラー処理を直す** (`SystemMain.java` の `sc.nextInt()` 全般)。`nextLine()` で読んで `Integer.parseInt` を try-catch するヘルパを作ります。種別選択でも `-1` を受け付けます。
4. **パッケージ名・メソッド名を規約に合わせる**: `library`/`manager`/`member` に小文字化します(`Library.java`、`Book.java`、`Manager.java`、`Member.java`、`Student.java`、`Teacher.java`)。`id()` を `getId()` に、`setMemberList` を `addMember` に、`direc` を `author` に直します。会員の同一性判定は ID を使います。
5. **リポジトリを整理する**: `src/メモ.java` を削除し、メモは `docs/` の `.md` に移します。`bin/` と `.settings/` を `git rm --cached` し、`.gitignore` を追加します。可能なら JUnit で `Member.borrow`(上限・二重貸出・返却)のテストを書きます。

## まとめ

動くものになっていて、構造の意識も見えます。次の段階は「機能を足す」より、重複を消して、ロジックを `Scanner` から切り離すことです。それだけで実務基準は 30 → 55〜60 点程度まで上がる見込みです。
