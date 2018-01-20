import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ope {
	Planner Planner = new Planner();

	public static void main(String args[]) {
		(new Ope()).start();
	}

	public void start() {
		initOperators();
		Vector goalList = initGoalList();
		Vector initialState = initInitialState();
		Hashtable theBinding = new Hashtable();
		Planner.plan = new Vector();
		Planner.planning(goalList, initialState, theBinding, false);
		System.out.println("***** This is a plan! *****");
		for (int i = 0; i < Planner.plan.size(); i++) {
			Operator op = (Operator) Planner.plan.elementAt(i);
			System.out.println((op.instantiate(theBinding)).name);
		}
	}

	// "clear -"を確認してから次のオペレータを探さないと、好き勝手動き回れてしまい、だめ
	
	private void initOperators() {
		Planner.operators = new Vector();
		Vector operators = Planner.operators;

		// OPERATOR 1 (スタートからゴールするまでの全ての手順をリストに格納)
		// / NAME
		String name1 = new String("Start");
		// / IF
		Vector ifList1 = new Vector();
//		ifList1.addElement(new String(""));
		// / ADD-LIST
		Vector addList1 = new Vector();
//		addList1.addElement(new String("move 1"));
		// / DELETE-LIST
		Vector deleteList1 = new Vector();
		deleteList1.addElement(new String("Start"));
		Operator operator1 = new Operator(name1, ifList1, addList1, deleteList1);
		operators.addElement(operator1);

		// OPERATOR 2 (１の終了条件)
		// / NAME
		String name2 = new String("Clear move 1");
		// / IF
		Vector ifList2 = new Vector();
		ifList2.addElement(new String("1 at ( 0 , 0 )"));
//		ifList2.addElement(new String("move 1"));
		// / ADD-LIST
		Vector addList2 = new Vector();
//		addList2.addElement(new String("adjacent 2 and 3"));
		addList2.addElement(new String("move 1"));
		// / DELETE-LIST
		Vector deleteList2 = new Vector();
//		deleteList2.addElement(new String("move 1"));
		Operator operator2 = new Operator(name2, ifList2, addList2, deleteList2);
		operators.addElement(operator2);

		/*
		// OPERATOR 3 (２と３の隣接)
		// / NAME
		String name3 = new String("Clear adjacent 2 and 3");
		// / IF
		Vector ifList3 = new Vector();
		ifList3.addElement(new String("2 at ( 1 , 0 )"));
		ifList3.addElement(new String("3 at ( 2 , 0 )"));
		ifList3.addElement(new String("adjacent 2 and 3"));
		// / ADD-LIST
		Vector addList3 = new Vector();
		addList3.addElement(new String("make first line"));
		// / DELETE-LIST
		Vector deleteList3 = new Vector();
//		deleteList3.addElement(new String("adjacent 2 and 3"));
		Operator operator3 = new Operator(name3, ifList3, addList3, deleteList3);
		operators.addElement(operator3);
		*/

		// OPERATOR 3 (1行目の完成)
		// / NAME
		String name3 = new String("Clear make first line");
		// / IF
		Vector ifList3 = new Vector();
		ifList3.addElement(new String("2 at ( 1 , 0 )"));
		ifList3.addElement(new String("3 at ( 2 , 0 )"));
		// / ADD-LIST
		Vector addList3 = new Vector();
//		addList4.addElement(new String("adjacent 4 and 7"));
		addList3.addElement(new String("make first line"));
		// / DELETE-LIST
		Vector deleteList3 = new Vector();
//		deleteList4.addElement(new String("make first line"));
		Operator operator3 = new Operator(name3, ifList3, addList3, deleteList3);
		operators.addElement(operator3);

		/*
		// OPERATOR 5 (4と7の隣接)
		// / NAME
		String name5 = new String("Clear adjacent 4 and 7");
		// / IF
		Vector ifList5 = new Vector();
		ifList5.addElement(new String("4 at ( 0 , 1 )"));
		ifList5.addElement(new String("7 at ( 0 , 2 )"));//or (x,y+1)
		ifList5.addElement(new String("adjacent 4 and 7"));
		// / ADD-LIST
		Vector addList5 = new Vector();
		addList5.addElement(new String("make first column"));
		// / DELETE-LIST
		Vector deleteList5 = new Vector();
//		deleteList5.addElement(new String("adjacent 4 and 7"));
		Operator operator5 = new Operator(name5, ifList5, addList5, deleteList5);
		operators.addElement(operator5);
		*/

		// OPERATOR 4 (1列目の完成)
		// / NAME
		String name4 = new String("Clear make first column");
		// / IF
		Vector ifList4 = new Vector();
		ifList4.addElement(new String("4 at ( 0 , 1 )"));
		ifList4.addElement(new String("7 at ( 0 , 2 )"));
		// / ADD-LIST
		Vector addList4 = new Vector();
		addList4.addElement(new String("make first column"));
		// / DELETE-LIST
		Vector deleteList4 = new Vector();
//		deleteList6.addElement(new String("make first column"));
		Operator operator4 = new Operator(name4, ifList4, addList4, deleteList4);
		operators.addElement(operator4);

		// OPERATOR 5 (2行目の完成)
		// / NAME
		String name5 = new String("Clear make second line");
		// / IF
		Vector ifList5 = new Vector();
		ifList5.addElement(new String("5 at ( 1 , 1 )"));
		ifList5.addElement(new String("6 at ( 2 , 1 )"));
		// / ADD-LIST
		Vector addList5 = new Vector();
		addList5.addElement(new String("make second line"));
		// / DELETE-LIST
		Vector deleteList5 = new Vector();
//		deleteList7.addElement(new String("make second line"));
		Operator operator5 = new Operator(name5, ifList5, addList5, deleteList5);
		operators.addElement(operator5);

		// OPERATOR 6 (3列目の完成)
		// / NAME
		String name6 = new String("Clear make third line");
		// / IF
		Vector ifList6 = new Vector();
		ifList6.addElement(new String("8 at ( 1 , 2 )"));
		ifList6.addElement(new String("( 2 , 2 ) is clear"));
		// / ADD-LIST
		Vector addList6 = new Vector();
		addList6.addElement(new String("make third line"));
		// / DELETE-LIST
		Vector deleteList6 = new Vector();
//		deleteList8.addElement(new String("make third line"));
		Operator operator6 = new Operator(name6, ifList6, addList6, deleteList6);
		operators.addElement(operator6);

		// OPERATOR 51 (move1)
		// / NAME
		String name51 = new String("move 1-2");
		// / IF
		Vector ifList51 = new Vector();
		ifList51.addElement(new String("?x at ( 0 , 0 )"));
		ifList51.addElement(new String("( 1 , 0 ) is clear"));
		// / ADD-LIST
		Vector addList51 = new Vector();
		addList51.addElement(new String("?x at ( 1 , 0 )"));
		addList51.addElement(new String("( 0 , 0 ) is clear"));
		// / DELETE-LIST
		Vector deleteList51 = new Vector();
		deleteList51.addElement(new String("?x at ( 0 , 0 )"));
		deleteList51.addElement(new String("( 1 , 0 ) is clear"));
		Operator operator51 = new Operator(name51, ifList51, addList51, deleteList51);
		operators.addElement(operator51);

		// OPERATOR 52 (move2)
		// / NAME
		String name52 = new String("move 1-4");
		// / IF
		Vector ifList52 = new Vector();
		ifList52.addElement(new String("?x at ( 0 , 0 )"));
		ifList52.addElement(new String("( 0 , 1 ) is clear"));
		// / ADD-LIST
		Vector addList52 = new Vector();
		addList52.addElement(new String("?x at ( 0 , 1 )"));
		addList52.addElement(new String("( 0 , 0 ) is clear"));
		// / DELETE-LIST
		Vector deleteList52 = new Vector();
		deleteList52.addElement(new String("?x at ( 0 , 0 )"));
		deleteList52.addElement(new String("( 0 , 1 ) is clear"));
		Operator operator52 = new Operator(name52, ifList52, addList52, deleteList52);
		operators.addElement(operator52);

		// OPERATOR 53 (move3)
		// / NAME
		String name53 = new String("move 2-1");
		// / IF
		Vector ifList53 = new Vector();
		ifList53.addElement(new String("?x at ( 1 , 0 )"));
		ifList53.addElement(new String("( 0 , 0 ) is clear"));
		// / ADD-LIST
		Vector addList53 = new Vector();
		addList53.addElement(new String("?x at ( 0 , 0 )"));
		addList53.addElement(new String("( 1 , 0 ) is clear"));
		// / DELETE-LIST
		Vector deleteList53 = new Vector();
		deleteList53.addElement(new String("?x at ( 1 , 0 )"));
		deleteList53.addElement(new String("( 0 , 0 ) is clear"));
		Operator operator53 = new Operator(name53, ifList53, addList53, deleteList53);
		operators.addElement(operator53);

		// OPERATOR 54 (move4)
		// / NAME
		String name54 = new String("move 2-5");
		// / IF
		Vector ifList54 = new Vector();
		ifList54.addElement(new String("?x at ( 1 , 0 )"));
		ifList54.addElement(new String("( 1 , 1 ) is clear"));
		// / ADD-LIST
		Vector addList54 = new Vector();
		addList54.addElement(new String("?x at ( 1 , 1 )"));
		addList54.addElement(new String("( 1 , 0 ) is clear"));
		// / DELETE-LIST
		Vector deleteList54 = new Vector();
		deleteList54.addElement(new String("?x at ( 1 , 0 )"));
		deleteList54.addElement(new String("( 1 , 1 ) is clear"));
		Operator operator54 = new Operator(name54, ifList54, addList54, deleteList54);
		operators.addElement(operator54);

		// OPERATOR 55 (move5)
		// / NAME
		String name55 = new String("move 2-3");
		// / IF
		Vector ifList55 = new Vector();
		ifList55.addElement(new String("?x at ( 1 , 0 )"));
		ifList55.addElement(new String("( 2 , 0 ) is clear"));
		// / ADD-LIST
		Vector addList55 = new Vector();
		addList55.addElement(new String("?x at ( 2 , 0 )"));
		addList55.addElement(new String("( 1 , 0 ) is clear"));
		// / DELETE-LIST
		Vector deleteList55 = new Vector();
		deleteList55.addElement(new String("?x at ( 1 , 0 )"));
		deleteList55.addElement(new String("( 2 , 0 ) is clear"));
		Operator operator55 = new Operator(name55, ifList55, addList55, deleteList55);
		operators.addElement(operator55);

		// OPERATOR 56 (move6)
		// / NAME
		String name56 = new String("move 3-2");
		// / IF
		Vector ifList56 = new Vector();
		ifList56.addElement(new String("?x at ( 2 , 0 )"));
		ifList56.addElement(new String("( 1 , 0 ) is clear"));
		// / ADD-LIST
		Vector addList56 = new Vector();
		addList56.addElement(new String("?x at ( 1 , 0 )"));
		addList56.addElement(new String("( 2 , 0 ) is clear"));
		// / DELETE-LIST
		Vector deleteList56 = new Vector();
		deleteList56.addElement(new String("?x at ( 2 , 0 )"));
		deleteList56.addElement(new String("( 1 , 0 ) is clear"));
		Operator operator56 = new Operator(name56, ifList56, addList56, deleteList56);
		operators.addElement(operator56);

		// OPERATOR 57 (move7)
		// / NAME
		String name57 = new String("move 3-6");
		// / IF
		Vector ifList57 = new Vector();
		ifList57.addElement(new String("?x at ( 2 , 0 )"));
		ifList57.addElement(new String("( 2 , 1 ) is clear"));
		// / ADD-LIST
		Vector addList57 = new Vector();
		addList57.addElement(new String("?x at ( 2 , 1 )"));
		addList57.addElement(new String("( 2 , 0 ) is clear"));
		// / DELETE-LIST
		Vector deleteList57 = new Vector();
		deleteList57.addElement(new String("?x at ( 2 , 0 )"));
		deleteList57.addElement(new String("( 2 , 1 ) is clear"));
		Operator operator57 = new Operator(name57, ifList57, addList57, deleteList57);
		operators.addElement(operator57);

		// OPERATOR 58 (move8)
		// / NAME
		String name58 = new String("move 4-1");
		// / IF
		Vector ifList58 = new Vector();
		ifList58.addElement(new String("?x at ( 0 , 1 )"));
		ifList58.addElement(new String("( 0 , 0 ) is clear"));
		// / ADD-LIST
		Vector addList58 = new Vector();
		addList58.addElement(new String("?x at ( 0 , 0 )"));
		addList58.addElement(new String("( 0 , 1 ) is clear"));
		// / DELETE-LIST
		Vector deleteList58 = new Vector();
		deleteList58.addElement(new String("?x at ( 0 , 1 )"));
		deleteList58.addElement(new String("( 0 , 0 ) is clear"));
		Operator operator58 = new Operator(name58, ifList58, addList58, deleteList58);
		operators.addElement(operator58);

		// OPERATOR 59 (move9)
		// / NAME
		String name59 = new String("move 4-5");
		// / IF
		Vector ifList59 = new Vector();
		ifList59.addElement(new String("?x at ( 0 , 1 )"));
		ifList59.addElement(new String("( 1 , 1 ) is clear"));
		// / ADD-LIST
		Vector addList59 = new Vector();
		addList59.addElement(new String("?x at ( 1 , 1 )"));
		addList59.addElement(new String("( 0 , 1 ) is clear"));
		// / DELETE-LIST
		Vector deleteList59 = new Vector();
		deleteList59.addElement(new String("?x at ( 0 , 1 )"));
		deleteList59.addElement(new String("( 1 , 1 ) is clear"));
		Operator operator59 = new Operator(name59, ifList59, addList59, deleteList59);
		operators.addElement(operator59);

		// OPERATOR 60 (move10)
		// / NAME
		String name60 = new String("move 4-7");
		// / IF
		Vector ifList60 = new Vector();
		ifList60.addElement(new String("?x at ( 0 , 1 )"));
		ifList60.addElement(new String("( 0 , 2 ) is clear"));
		// / ADD-LIST
		Vector addList60 = new Vector();
		addList60.addElement(new String("?x at ( 0 , 2 )"));
		addList60.addElement(new String("( 0 , 1 ) is clear"));
		// / DELETE-LIST
		Vector deleteList60 = new Vector();
		deleteList60.addElement(new String("?x at ( 0 , 1 )"));
		deleteList60.addElement(new String("( 0 , 2 ) is clear"));
		Operator operator60 = new Operator(name60, ifList60, addList60, deleteList60);
		operators.addElement(operator60);

		// OPERATOR 61 (move11)
		// / NAME
		String name61 = new String("move 5-4");
		// / IF
		Vector ifList61 = new Vector();
		ifList61.addElement(new String("?x at ( 1 , 1 )"));
		ifList61.addElement(new String("( 0 , 1 ) is clear"));
		// / ADD-LIST
		Vector addList61 = new Vector();
		addList61.addElement(new String("?x at ( 0 , 1 )"));
		addList61.addElement(new String("( 1 , 1 ) is clear"));
		// / DELETE-LIST
		Vector deleteList61 = new Vector();
		deleteList61.addElement(new String("?x at ( 1 , 1 )"));
		deleteList61.addElement(new String("( 0 , 1 ) is clear"));
		Operator operator61 = new Operator(name61, ifList61, addList61, deleteList61);
		operators.addElement(operator61);

		// OPERATOR 62 (move12)
		// / NAME
		String name62 = new String("move 5-8");
		// / IF
		Vector ifList62 = new Vector();
		ifList62.addElement(new String("?x at ( 1 , 1 )"));
		ifList62.addElement(new String("( 1 , 2 ) is clear"));
		// / ADD-LIST
		Vector addList62 = new Vector();
		addList62.addElement(new String("?x at ( 1 , 2 )"));
		addList62.addElement(new String("( 1 , 1 ) is clear"));
		// / DELETE-LIST
		Vector deleteList62 = new Vector();
		deleteList62.addElement(new String("?x at ( 1 , 1 )"));
		deleteList62.addElement(new String("( 1 , 2 ) is clear"));
		Operator operator62 = new Operator(name62, ifList62, addList62, deleteList62);
		operators.addElement(operator62);

		// OPERATOR 63 (move13)
		// / NAME
		String name63 = new String("move 5-6");
		// / IF
		Vector ifList63 = new Vector();
		ifList63.addElement(new String("?x at ( 1 , 1 )"));
		ifList63.addElement(new String("( 2 , 1 ) is clear"));
		// / ADD-LIST
		Vector addList63 = new Vector();
		addList63.addElement(new String("?x at ( 2 , 1 )"));
		addList63.addElement(new String("( 1 , 1 ) is clear"));
		// / DELETE-LIST
		Vector deleteList63 = new Vector();
		deleteList63.addElement(new String("?x at ( 1 , 1 )"));
		deleteList63.addElement(new String("( 2 , 1 ) is clear"));
		Operator operator63 = new Operator(name63, ifList63, addList63, deleteList63);
		operators.addElement(operator63);

		// OPERATOR 64 (move14)
		// / NAME
		String name64 = new String("move 5-2");
		// / IF
		Vector ifList64 = new Vector();
		ifList64.addElement(new String("?x at ( 1 , 1 )"));
		ifList64.addElement(new String("( 1 , 0 ) is clear"));
		// / ADD-LIST
		Vector addList64 = new Vector();
		addList64.addElement(new String("?x at ( 1 , 0 )"));
		addList64.addElement(new String("( 1 , 1 ) is clear"));
		// / DELETE-LIST
		Vector deleteList64 = new Vector();
		deleteList64.addElement(new String("?x at ( 1 , 1 )"));
		deleteList64.addElement(new String("( 1 , 0 ) is clear"));
		Operator operator64 = new Operator(name64, ifList64, addList64, deleteList64);
		operators.addElement(operator64);

		// OPERATOR 65 (move15)
		// / NAME
		String name65 = new String("move 6-3");
		// / IF
		Vector ifList65 = new Vector();
		ifList65.addElement(new String("?x at ( 2 , 1 )"));
		ifList65.addElement(new String("( 2 , 0 ) is clear"));
		// / ADD-LIST
		Vector addList65 = new Vector();
		addList65.addElement(new String("?x at ( 2 , 0 )"));
		addList65.addElement(new String("( 2 , 1 ) is clear"));
		// / DELETE-LIST
		Vector deleteList65 = new Vector();
		deleteList65.addElement(new String("?x at ( 2 , 1 )"));
		deleteList65.addElement(new String("( 2 , 0 ) is clear"));
		Operator operator65 = new Operator(name65, ifList65, addList65, deleteList65);
		operators.addElement(operator65);

		// OPERATOR 66 (move16)
		// / NAME
		String name66 = new String("move 6-5");
		// / IF
		Vector ifList66 = new Vector();
		ifList66.addElement(new String("?x at ( 2 , 1 )"));
		ifList66.addElement(new String("( 1 , 1 ) is clear"));
		// / ADD-LIST
		Vector addList66 = new Vector();
		addList66.addElement(new String("?x at ( 1 , 1 )"));
		addList66.addElement(new String("( 2 , 1 ) is clear"));
		// / DELETE-LIST
		Vector deleteList66 = new Vector();
		deleteList66.addElement(new String("?x at ( 2 , 1 )"));
		deleteList66.addElement(new String("( 1 , 1 ) is clear"));
		Operator operator66 = new Operator(name66, ifList66, addList66, deleteList66);
		operators.addElement(operator66);

		// OPERATOR 67 (move17)
		// / NAME
		String name67 = new String("move 6-9");
		// / IF
		Vector ifList67 = new Vector();
		ifList67.addElement(new String("?x at ( 2 , 1 )"));
		ifList67.addElement(new String("( 2 , 2 ) is clear"));
		// / ADD-LIST
		Vector addList67 = new Vector();
		addList67.addElement(new String("?x at ( 2 , 2 )"));
		addList67.addElement(new String("( 2 , 1 ) is clear"));
		// / DELETE-LIST
		Vector deleteList67 = new Vector();
		deleteList67.addElement(new String("?x at ( 2 , 1 )"));
		deleteList67.addElement(new String("( 2 , 2 ) is clear"));
		Operator operator67 = new Operator(name67, ifList67, addList67, deleteList67);
		operators.addElement(operator67);

		// OPERATOR 68 (move18)
		// / NAME
		String name68 = new String("move 7-4");
		// / IF
		Vector ifList68 = new Vector();
		ifList68.addElement(new String("?x at ( 0 , 2 )"));
		ifList68.addElement(new String("( 0 , 1 ) is clear"));
		// / ADD-LIST
		Vector addList68 = new Vector();
		addList68.addElement(new String("?x at ( 0 , 1 )"));
		addList68.addElement(new String("( 0 , 2 ) is clear"));
		// / DELETE-LIST
		Vector deleteList68 = new Vector();
		deleteList68.addElement(new String("?x at ( 0 , 2 )"));
		deleteList68.addElement(new String("( 0 , 1 ) is clear"));
		Operator operator68 = new Operator(name68, ifList68, addList68, deleteList68);
		operators.addElement(operator68);

		// OPERATOR 69 (move19)
		// / NAME
		String name69 = new String("move 7-8");
		// / IF
		Vector ifList69 = new Vector();
		ifList69.addElement(new String("?x at ( 0 , 2 )"));
		ifList69.addElement(new String("( 1 , 2 ) is clear"));
		// / ADD-LIST
		Vector addList69 = new Vector();
		addList69.addElement(new String("?x at ( 1 , 2 )"));
		addList69.addElement(new String("( 0 , 2 ) is clear"));
		// / DELETE-LIST
		Vector deleteList69 = new Vector();
		deleteList69.addElement(new String("?x at ( 0 , 2 )"));
		deleteList69.addElement(new String("( 1 , 2 ) is clear"));
		Operator operator69 = new Operator(name69, ifList69, addList69, deleteList69);
		operators.addElement(operator69);

		// OPERATOR 70 (move20)
		// / NAME
		String name70 = new String("move 8-7");
		// / IF
		Vector ifList70 = new Vector();
		ifList70.addElement(new String("?x at ( 1 , 2 )"));
		ifList70.addElement(new String("( 0 , 2 ) is clear"));
		// / ADD-LIST
		Vector addList70 = new Vector();
		addList70.addElement(new String("?x at ( 0 , 2 )"));
		addList70.addElement(new String("( 1 , 2 ) is clear"));
		// / DELETE-LIST
		Vector deleteList70 = new Vector();
		deleteList70.addElement(new String("?x at ( 1 , 2 )"));
		deleteList70.addElement(new String("( 0 , 2 ) is clear"));
		Operator operator70 = new Operator(name70, ifList70, addList70, deleteList70);
		operators.addElement(operator70);

		// OPERATOR 71 (move21)
		// / NAME
		String name71 = new String("move 8-5");
		// / IF
		Vector ifList71 = new Vector();
		ifList71.addElement(new String("?x at ( 1 , 2 )"));
		ifList71.addElement(new String("( 1 , 1 ) is clear"));
		// / ADD-LIST
		Vector addList71 = new Vector();
		addList71.addElement(new String("?x at ( 1 , 1 )"));
		addList71.addElement(new String("( 1 , 2 ) is clear"));
		// / DELETE-LIST
		Vector deleteList71 = new Vector();
		deleteList71.addElement(new String("?x at ( 1 , 2 )"));
		deleteList71.addElement(new String("( 1 , 1 ) is clear"));
		Operator operator71 = new Operator(name71, ifList71, addList71, deleteList71);
		operators.addElement(operator71);

		// OPERATOR 72 (move22)
		// / NAME
		String name72 = new String("move 8-9");
		// / IF
		Vector ifList72 = new Vector();
		ifList72.addElement(new String("?x at ( 1 , 2 )"));
		ifList72.addElement(new String("( 2 , 2 ) is clear"));
		// / ADD-LIST
		Vector addList72 = new Vector();
		addList72.addElement(new String("?x at ( 2 , 2 )"));
		addList72.addElement(new String("( 1 , 2 ) is clear"));
		// / DELETE-LIST
		Vector deleteList72 = new Vector();
		deleteList72.addElement(new String("?x at ( 1 , 2 )"));
		deleteList72.addElement(new String("( 2 , 2 ) is clear"));
		Operator operator72 = new Operator(name72, ifList72, addList72, deleteList72);
		operators.addElement(operator72);

		// OPERATOR 73 (move23)
		// / NAME
		String name73 = new String("move 9-6");
		// / IF
		Vector ifList73 = new Vector();
		ifList73.addElement(new String("?x at ( 2 , 2 )"));
		ifList73.addElement(new String("( 2 , 1 ) is clear"));
		// / ADD-LIST
		Vector addList73 = new Vector();
		addList73.addElement(new String("?x at ( 2 , 1 )"));
		addList73.addElement(new String("( 2 , 2 ) is clear"));
		// / DELETE-LIST
		Vector deleteList73 = new Vector();
		deleteList73.addElement(new String("?x at ( 2 , 2 )"));
		deleteList73.addElement(new String("( 2 , 1 ) is clear"));
		Operator operator73 = new Operator(name73, ifList73, addList73, deleteList73);
		operators.addElement(operator73);

		// OPERATOR 74 (move24)
		// / NAME
		String name74 = new String("move 9-8");
		// / IF
		Vector ifList74 = new Vector();
		ifList74.addElement(new String("?x at ( 2 , 2 )"));
		ifList74.addElement(new String("( 1 , 2 ) is clear"));
		// / ADD-LIST
		Vector addList74 = new Vector();
		addList74.addElement(new String("?x at ( 1 , 2 )"));
		addList74.addElement(new String("( 2 , 2 ) is clear"));
		// / DELETE-LIST
		Vector deleteList74 = new Vector();
		deleteList74.addElement(new String("?x at ( 2 , 2 )"));
		deleteList74.addElement(new String("( 1 , 2 ) is clear"));
		Operator operator74 = new Operator(name74, ifList74, addList74, deleteList74);
		operators.addElement(operator74);

	}

	private Vector initGoalList() {
		Vector goalList = new Vector();
		//なんか途中のゴールリストに変数がまじってて解けない
		goalList.addElement("move 1");
		goalList.addElement(new String("make first line"));
		goalList.addElement(new String("make first column"));
		goalList.addElement(new String("make second line"));
		goalList.addElement(new String("make third line"));

		return goalList;
	}

	private Vector initInitialState() {
		Vector initialState = new Vector();
		initialState.addElement("Start");

		initialState.addElement("1 at ( 1 , 0 )");//(0,0)
		initialState.addElement("2 at ( 2 , 0 )");//(1,0)
		initialState.addElement("3 at ( 2 , 1 )");//(2,0)
		initialState.addElement("4 at ( 0 , 2 )");//(0,1)
		initialState.addElement("5 at ( 0 , 1 )");//(1,1)
		initialState.addElement("6 at ( 1 , 1 )");//(2,1)
		initialState.addElement("7 at ( 1 , 2 )");//(0,2)
		initialState.addElement("8 at ( 2 , 2 )");//(1,2)
		initialState.addElement("( 0 , 0 ) is clear");//(2,2)

		/*
		initialState.addElement("1 at ( 0 , 0 )");//(0,0)
		initialState.addElement("2 at ( 1 , 0 )");//(1,0)
		initialState.addElement("3 at ( 2 , 0 )");//(2,0)
		initialState.addElement("4 at ( 0 , 2 )");//(0,1)
		initialState.addElement("5 at ( 1 , 1 )");//(1,1)
		initialState.addElement("6 at ( 2 , 1 )");//(2,1)
		initialState.addElement("7 at ( 1 , 2 )");//(0,2)
		initialState.addElement("8 at ( 2 , 2 )");//(1,2)
		initialState.addElement("( 0 , 1 ) is clear");//(2,2)
		*/

		return initialState;
	}
}
