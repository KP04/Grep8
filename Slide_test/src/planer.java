import java.io.File;
import java.util.*;

public class planer {
	Vector operators;
	Random rand;
	Vector plan;
	ArrayList<String> process = new ArrayList<String>();
	int stepCount = 0;

	public static void main(String argv[]) {
		(new planer()).start();
	}

	planer() {
		rand = new Random();
	}

	/*
	PlannerForSlide(PlannerGUI pgui) {
		rand = new Random();
		this.pgui = pgui;
	}
	*/

	public void start() {
		initOperators();
		Vector goalList = initGoalList();
		Vector initialState = initInitialState();

		Hashtable theBinding = new Hashtable();
		plan = new Vector();
		planning(goalList, initialState, theBinding, null, true);

		System.out.println("***** This is a plan! *****");
		for (int i = 0; i < plan.size(); i++) {
			Operator op = (Operator) plan.elementAt(i);
			System.out.println((op.instantiate(theBinding)).name);
		}

	}

	/*
	public void startWithGUI() {
		initOperators();
		Vector goalList = initGoalList(pgui);
		Vector initialState = initInitialState(pgui);
		System.out.println(initialState);
		Hashtable theBinding = new Hashtable();
		plan = new Vector();
		planning(goalList, initialState, theBinding, null, true);
		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph());
		Operator op1, op2;
		String s1, s2;
		for (int i = 0; i < plan.size(); i++) {
			if (i != 0) {
				op1 = (Operator) plan.elementAt(i - 1);
				op2 = (Operator) plan.elementAt(i);
				s1 = (op1.instantiate(theBinding)).name.replace(' ', '_') + "_"
						+ i;
				s2 = (op2.instantiate(theBinding)).name.replace(' ', '_') + "_"
						+ (i + 1);
				gv.addln(s1 + " -> " + s2 + ";");
			}
		}
		String type = "png";
		String repesentationType = "dot";
		File out = new File("tmp/simple" + "." + type);
		gv.writeGraphToFile(
				gv.getGraph(gv.getDotSource(), type, repesentationType), out);
	}
	*/

	public void printGUIMessage(String str){
		// GUIに表示する用の関数
	}

	boolean planning(Vector theGoalList,
					 Vector theCurrentState,
					 Hashtable theBinding,
					 Operator nextOperator,
					 boolean isFirstStep) 
	{
		System.out.println("*** GOALS ***" + theGoalList);
		System.out.println("*** STATE ***" + theCurrentState);
		System.out.println("*** BIND ***" + theBinding);
		if (theGoalList.size() == 1) {
			String aGoal = (String) theGoalList.elementAt(0);
			if (planningAGoal(aGoal, theCurrentState, theBinding, 0, nextOperator) != -1) {
				return true;
			} else {
				return false;
			}
		} else {
			// サブゴールの先頭を取り出し
			String aGoal = (String) theGoalList.elementAt(0);

			// おそらく check point. ゴール状態を満たすまでに探索したオペレータのインデックスをtepPointから更新しつつ保持
			int cPoint = 0;
			while (cPoint < operators.size()) {
				System.out.println("cPoint:" + cPoint);

				// Store original binding
				Hashtable orgBinding = new Hashtable();
				for (Enumeration e = theBinding.keys(); e.hasMoreElements();) {
					String key = (String) e.nextElement();
					String value = (String) theBinding.get(key);
					orgBinding.put(key, value);
				}

				// Store original state
				Vector orgState = new Vector();
				for (int i = 0; i < theCurrentState.size(); i++) {
					orgState.addElement(theCurrentState.elementAt(i));
				}

				int tmpPoint = planningAGoal(aGoal, theCurrentState, theBinding, cPoint, nextOperator);

				// オペレータのインデックス+1 (そもそも現状にマッチングしてたら0)
				System.out.println("tmpPoint: " + tmpPoint);

				if (tmpPoint != -1) {
					theGoalList.removeElementAt(0);
					if(isFirstStep)
					{
						//後ろに逆順に挿入
						String state = (String)theCurrentState.get(0);
						theCurrentState.removeElementAt(0);
						theCurrentState.add(theCurrentState.size() - stepCount++, state);
					}
					System.out.println(theCurrentState);
					if (planning(theGoalList, theCurrentState, theBinding, nextOperator, isFirstStep)) {
						System.out.println("Success !");
						return true;
					} else {
						cPoint = tmpPoint;
						// System.out.println("Fail::"+cPoint);
						theGoalList.insertElementAt(aGoal, 0);

						theBinding.clear();
						for (Enumeration e = orgBinding.keys(); e
								.hasMoreElements();) {
							String key = (String) e.nextElement();
							String value = (String) orgBinding.get(key);
							theBinding.put(key, value);
						}
						theCurrentState.removeAllElements();
						for (int i = 0; i < orgState.size(); i++) {
							theCurrentState.addElement(orgState.elementAt(i));
						}
					}
				} else {
					theBinding.clear();
					for (Enumeration e = orgBinding.keys(); e.hasMoreElements();) {
						String key = (String) e.nextElement();
						String value = (String) orgBinding.get(key);
						theBinding.put(key, value);
					}
					theCurrentState.removeAllElements();
					for (int i = 0; i < orgState.size(); i++) {
						theCurrentState.addElement(orgState.elementAt(i));
					}
					return false;
				}
			}
			return false;
		}
	}

	private int planningAGoal(String theGoal,
							  Vector theCurrentState,
							  Hashtable theBinding,
							  int cPoint,
							  Operator nextOperator) {
		System.out.println("**" + theGoal);
		int size = theCurrentState.size();
		for (int i = 0; i < size; i++) {
			String aState = (String) theCurrentState.elementAt(i);
			// 現状にマッチングする状態が来たら返す
			int tempUniqueNum = uniqueNum;
			boolean isRelatedNextOperator = false;
			if(nextOperator != null){
				isRelatedNextOperator = nextOperator.getIsRelatedNextOperator();
			}
			if ((new Unifier()).unify(theGoal, aState, theBinding, isRelatedNextOperator, null, null, null)) {
				return 0;
			}
		}

		/* ***************** 乱数の個所 ****************************** */

		/*
		int randInt = Math.abs(rand.nextInt()) % operators.size();
		 * while(randInt == tempOpeNo.get(step % 2)) { randInt =
		 * Math.abs(rand.nextInt()) % operators.size(); } tempOpeNo.set(step %
		 * 2, randInt);
		// System.out.println("Before:\n" + operators);
		Operator op = (Operator) operators.elementAt(randInt);
		// System.out.println(op);
		operators.removeElementAt(randInt);
		operators.addElement(op);
		// System.out.println("After:\n" + operators);
		// System.out.println("Target:\n" + op);
		 * */

		/* *********************************************************** */

		/* 対になるオペレータの優先順位を上げる */
		// どっちかっていうと置くほうだけ変数束縛が対になるオペレータと同じ場合を考えたい
		if(nextOperator != null){
			Vector pairs = nextOperator.GetPairedOperatorList();
			for(int i = 0; i < pairs.size(); ++i)
			{
				Operator pair = (Operator) pairs.get(i);
				operators.remove(pair);
				// 先頭に持ってく
				operators.add(0, pair);
			}
		}

		int tempUniqueNum = uniqueNum;

		for (int i = cPoint; i < operators.size(); i++) {
			Operator targetOperator = (Operator) operators.elementAt(i);
			Operator anOperator = rename(targetOperator);

			// 現在のCurrent state, Binding, planをbackup
			Hashtable orgBinding = new Hashtable();
			for (Enumeration e = theBinding.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String value = (String) theBinding.get(key);
				orgBinding.put(key, value);
			}
			Vector orgState = new Vector();
			for (int j = 0; j < theCurrentState.size(); j++) {
				orgState.addElement(theCurrentState.elementAt(j));
			}
			Vector orgPlan = new Vector();
			for (int j = 0; j < plan.size(); j++) {
				orgPlan.addElement(plan.elementAt(j));
			}

			// 条件が満たされたとき、追加される状態を格納
			Vector addList = (Vector) anOperator.getAddList();

			for (int j = 0; j < addList.size(); j++) {
				boolean isRelatedNextOperator = false;
				if(nextOperator != null){
					isRelatedNextOperator = nextOperator.getIsRelatedNextOperator();
				}

				Operator newOperator = null;
				Vector newGoals = null;

				if ((new Unifier()).unify(theGoal,
						(String) addList.elementAt(j), theBinding, isRelatedNextOperator, anOperator, newOperator, newGoals)) {
					// 具体化し、あらたなゴールを生成
					//Operator newOperator = anOperator.instantiate(theBinding);
					//Vector newGoals = (Vector) newOperator.getIfList();
					//System.out.println(newOperator.name);

					// 何の根拠もないから、修正案件
					Operator op = (Operator) operators.elementAt(i);
					operators.removeElementAt(i);
					operators.addElement(op);

					// 再帰呼び出し
					/*
					 * ・案１
					 * 		呼び出し前に近傍を検索して状態の並び替え
					 *
					 * ・案２
					 * 		渡されたオペレータを使って、移動可能かを判定する
					 *
					 * ・案３(最有力)
					 * 		選ばれたオペレータを使って移動可能か判定する
					 *
					 * 		→上記のnewGoals作成時に、ifListから判定材料だけ抜き出して
					 * 		　動作を進める
					 */
					if (planning(newGoals, theCurrentState, theBinding, targetOperator, false)) {
						newOperator = newOperator.instantiate(theBinding);
						System.out.println(newOperator.name);
						plan.addElement(newOperator);
						theCurrentState = newOperator
								.applyState(theCurrentState);
						return i + 1;
					} else {
						// 失敗したら元に戻す．
						theBinding.clear();
						for (Enumeration e = orgBinding.keys(); e
								.hasMoreElements();) {
							String key = (String) e.nextElement();
							String value = (String) orgBinding.get(key);
							theBinding.put(key, value);
						}
						theCurrentState.removeAllElements();
						for (int k = 0; k < orgState.size(); k++) {
							theCurrentState.addElement(orgState.elementAt(k));
						}
						plan.removeAllElements();
						for (int k = 0; k < orgPlan.size(); k++) {
							plan.addElement(orgPlan.elementAt(k));
						}
					}
				}
			}
		}
		return -1;
	}

	int uniqueNum = 0;

	// 変数を区別したうえで、Operatorを生成
	private Operator rename(Operator theOperator) {
		Operator newOperator = theOperator.getRenamedOperator(uniqueNum);
		uniqueNum = uniqueNum + 1;
		return newOperator;
	}

	private Vector initGoalList() {
		Vector goalList = new Vector();
		// バグ  //* 積みあがった状態から降ろすのはループしちゃうっぽい
		//goalList.addElement("ontable A");
		//goalList.addElement("C on B");

		 goalList.addElement("1 at ( 0 , 0 )");
		 goalList.addElement("2 at ( 1 , 0 )");
		 goalList.addElement("3 at ( 2 , 0 )");
		 goalList.addElement("4 at ( 0 , 1 )");
		 goalList.addElement("5 at ( 1 , 1 )");
		 goalList.addElement("6 at ( 2 , 1 )");
		 goalList.addElement("7 at ( 0 , 2 )");
		 goalList.addElement("8 at ( 1 , 2 )");
		 goalList.addElement("( 2 , 2 ) is clear");

		/*
		goalList.addElement("A on B");
		goalList.addElement("B on C");
		*/
		//Vector newGoalList = alignGoalList(goalList);
		//System.out.println(newGoalList);
		return goalList;
	}

	// ゴールリストを都合のいいように編集
	private Vector alignGoalList(Vector goalList)
	{
		Vector newGoalList = new Vector();
		ArrayList<String> allObjects = new ArrayList<String>();

		for(int index = 0; index < goalList.size(); ++index)
		{
			ArrayList<String> objects = new ArrayList<String>();
			boolean isOnState = false;
			StringTokenizer tokenizer = new StringTokenizer((String)goalList.get(index));
			String firstObject = "";
			while(tokenizer.hasMoreTokens())
			{
				String token = tokenizer.nextToken();
				if(!token.equals("on"))
				{
					objects.add(token);
					firstObject = token;
				}
				else
				{
					isOnState = true;
				}
			}

			int insertIndex = allObjects.size();

			if(allObjects.contains(firstObject))
			{
				insertIndex = allObjects.indexOf(firstObject);
				allObjects.remove(insertIndex);
			}

			if(isOnState)
			{
				allObjects.addAll(insertIndex, objects);
			}
			else
			{
				newGoalList.add((String)goalList.get(index));
			}
		}

		for(int index = allObjects.size() - 1; index > 0; --index)
		{
			String goal = allObjects.get(index - 1) + " on " + allObjects.get(index);
			newGoalList.add(goal);
		}

		return newGoalList;
	}

	/*
	private Vector initGoalList(PlannerGUI pgui) {
		Vector goalList = new Vector();
		String[] gList = pgui.goalTextArea.getText().split("\n");
		for (int i = 0; i < gList.length; i++) {
			goalList.addElement(gList[i]);
		}
		return goalList;
	}
	*/

	private Vector initInitialState() {
		Vector initialState = new Vector();

		 initialState.addElement("1 at ( 1 , 0 )");
		 initialState.addElement("2 at ( 1 , 1 )");
		 initialState.addElement("3 at ( 2 , 0 )");
		 initialState.addElement("4 at ( 0 , 1 )");
		 initialState.addElement("5 at ( 1 , 2 )");
		 initialState.addElement("6 at ( 2 , 1 )");
		 initialState.addElement("7 at ( 0 , 2 )");
		 initialState.addElement("8 at ( 2 , 2 )");
		 initialState.addElement("( 0 , 0 ) is clear");

		/*
		initialState.addElement("clear A");
		initialState.addElement("clear B");
		initialState.addElement("clear C");

		initialState.addElement("ontable A");
		initialState.addElement("ontable B");
		initialState.addElement("ontable C");
		initialState.addElement("handEmpty");
		*/
		return initialState;
	}

	/*
	private Vector initInitialState(PlannerGUI pgui) {
		Vector initialState = new Vector();
		String[] iList = pgui.initialTextArea.getText().split("\n");
		for (int i = 0; i < iList.length; i++) {
			initialState.addElement(iList[i]);
		}
		return initialState;
	}
	*/

	private void initOperators() {
		private void initOperators() {
			Planner.operators = new Vector();
			Vector operators = Planner.operators;
			
			// OPERATOR 1 (スタートからゴールするまでの全ての手順をリストに格納)
			// / NAME
			String name1 = new String("Start");
			// / IF
			Vector ifList1 = new Vector();
			ifList1.addElement(new String("Start"));
			// / ADD-LIST
			Vector addList1 = new Vector();
			addList1.addElement(new String("move 1"));
//			addList1.addElement(new String("adjacent 2 and 3"));
//			addList1.addElement(new String("make first line"));
//			addList1.addElement(new String("adjacent 4 and 7"));
//			addList1.addElement(new String("make first column"));
//			addList1.addElement(new String("make second line"));
//			addList1.addElement(new String("make third line"));
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
			ifList2.addElement(new String("Start"));
			// / ADD-LIST
			Vector addList2 = new Vector();
			addList2.addElement(new String("adjacent 2 and 3"));
			// / DELETE-LIST
			Vector deleteList2 = new Vector();
			deleteList2.addElement(new String("move 1"));
			Operator operator2 = new Operator(name2, ifList2, addList2, deleteList2);
			operators.addElement(operator2);
			
			/*
			// OPERATOR (１を右上に持って行く過程)
			// / NAME
			String name = new String("move 1");
			// / IF
			Vector ifList = new Vector();
			ifList.addElement(new String("1 at ( x , y )"));
			ifList.addElement(new String("( x-1 , y ) is clear"));//or (x,y-1)
			ifList.addElement(new String("move 1"));
			// / ADD-LIST
			Vector addList = new Vector();
			addList.addElement(new String("1 at ( x-1 , y )"));//or (x,y-1)
			addList.addElement(new String("( x , y ) is clear"));
			// / DELETE-LIST
			Vector deleteList = new Vector();
			deleteList.addElement(new String("1 at ( x , y )"));
			deleteList.addElement(new String("( x-1 , y ) is clear"));//or (x,y-1)
			Operator operator = new Operator(name, ifList, addList, deleteList);
			operators.addElement(operator);
			*/
			
			// OPERATOR 3 (２と３の隣接)
			// / NAME
			String name3 = new String("Clear adjacent 2 and 3");
			// / IF
			Vector ifList3 = new Vector();
			ifList3.addElement(new String("2 at ( 1 , 0 )"));
			ifList3.addElement(new String("3 at ( 2 , 0 )"));//or (x,y+1)
			ifList3.addElement(new String("adjacent 2 and 3"));
			// / ADD-LIST
			Vector addList3 = new Vector();
			addList3.addElement(new String("make first line"));
			// / DELETE-LIST
			Vector deleteList3 = new Vector();
			deleteList3.addElement(new String("adjacent 2 and 3"));
			Operator operator3 = new Operator(name3, ifList3, addList3, deleteList3);
			operators.addElement(operator3);
			
			// OPERATOR 4 (1行目の完成)
			// / NAME
			String name4 = new String("Clear make first line");
			// / IF
			Vector ifList4 = new Vector();
			ifList4.addElement(new String("2 at ( 1 , 0 )"));
			ifList4.addElement(new String("3 at ( 2 , 0 )"));
			ifList4.addElement(new String("make first line"));
			// / ADD-LIST
			Vector addList4 = new Vector();
			addList4.addElement(new String("adjacent 4 and 7"));
			// / DELETE-LIST
			Vector deleteList4 = new Vector();
			deleteList4.addElement(new String("make first line"));
			Operator operator4 = new Operator(name4, ifList4, addList4, deleteList4);
			operators.addElement(operator4);
			
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
			deleteList5.addElement(new String("adjacent 4 and 7"));
			Operator operator5 = new Operator(name5, ifList5, addList5, deleteList5);
			operators.addElement(operator5);
			
			// OPERATOR 6 (1列目の完成)
			// / NAME
			String name6 = new String("Clear make first column");
			// / IF
			Vector ifList6 = new Vector();
			ifList6.addElement(new String("4 at ( 0 , 1 )"));
			ifList6.addElement(new String("7 at ( 0 , 2 )"));
			ifList6.addElement(new String("make first column"));
			// / ADD-LIST
			Vector addList6 = new Vector();
			addList6.addElement(new String("make second line"));
			// / DELETE-LIST
			Vector deleteList6 = new Vector();
			deleteList6.addElement(new String("make first column"));
			Operator operator6 = new Operator(name6, ifList6, addList6, deleteList6);
			operators.addElement(operator6);
			
			// OPERATOR 7 (2行目の完成)
			// / NAME
			String name7 = new String("Clear make second line");
			// / IF
			Vector ifList7 = new Vector();
			ifList7.addElement(new String("5 at ( 1 , 1 )"));
			ifList7.addElement(new String("6 at ( 2 , 1 )"));
			ifList7.addElement(new String("make second line"));
			// / ADD-LIST
			Vector addList7 = new Vector();
			addList7.addElement(new String("make third line"));
			// / DELETE-LIST
			Vector deleteList7 = new Vector();
			deleteList7.addElement(new String("make second line"));
			Operator operator7 = new Operator(name7, ifList7, addList7, deleteList7);
			operators.addElement(operator7);
			
			// OPERATOR 8 (1列目の完成)
			// / NAME
			String name8 = new String("Clear make third line");
			// / IF
			Vector ifList8 = new Vector();
			ifList8.addElement(new String("8 at ( 1 , 2 )"));
			ifList8.addElement(new String("( 2 , 2 ) is clear"));
			ifList8.addElement(new String("make third line"));
			// / ADD-LIST
			Vector addList8 = new Vector();
			addList8.addElement(new String("Goal"));
			// / DELETE-LIST
			Vector deleteList8 = new Vector();
			deleteList8.addElement(new String("make third line"));
			Operator operator8 = new Operator(name8, ifList8, addList8, deleteList8);
			operators.addElement(operator8);
			
			// OPERATOR 51 (move1)
			// / NAME
			String name51 = new String("move 1-1");
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
			String name52 = new String("move 1-2");
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
			String name54 = new String("move 2-2");
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
			String name56 = new String("move 3-1");
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
			String name57 = new String("move 3-2");
			// / IF
			Vector ifList57 = new Vector();
			ifList57.addElement(new String("?x at ( 2 , 0 )"));
			ifList57.addElement(new String("( 2 , 1 ) is clear"));
			// / ADD-LIST
			Vector addList57 = new Vector();
			addList57.addElement(new String("?x at ( 2 , 0 )"));
			addList57.addElement(new String("( 2 , 1 ) is clear"));
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
			String name59 = new String("move 4-2");
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
			String name60 = new String("move 4-3");
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
			String name61 = new String("move 5-1");
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
			String name62 = new String("move 5-2");
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
			String name63 = new String("move 5-3");
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
			String name64 = new String("move 5-4");
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
			String name65 = new String("move 6-1");
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
			String name66 = new String("move 6-2");
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
			String name67 = new String("move 6-3");
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
			String name68 = new String("move 7-1");
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
			String name69 = new String("move 7-2");
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
			String name70 = new String("move 8-1");
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
			String name71 = new String("move 8-2");
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
			String name72 = new String("move 8-3");
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
			String name73 = new String("move 9-1");
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
			String name74 = new String("move 9-1");
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
}

class Operator {
	String name;
	Vector ifList;
	Vector addList;
	Vector deleteList;
	Vector pairedOperatorList;
	boolean isRelatedNextOperator;

	Operator(String theName,
			 Vector theIfList,
			 Vector theAddList,
			 Vector theDeleteList,
			 boolean theFlag) {
		name = theName;
		ifList = theIfList;
		addList = theAddList;
		deleteList = theDeleteList;
		pairedOperatorList = new Vector();
		isRelatedNextOperator = theFlag;
	}

	public Vector getAddList() {
		return addList;
	}

	public Vector getDeleteList() {
		return deleteList;
	}

	public Vector getIfList() {
		return ifList;
	}

	public Vector GetPairedOperatorList(){
		return pairedOperatorList;
	}

	public void addPairedOperator(Operator operator){
		pairedOperatorList.add(operator);
	}

	public boolean checkPairedOperator(Operator operator){
		return pairedOperatorList.contains(operator);
	}

	public boolean getIsRelatedNextOperator(){
		return isRelatedNextOperator;
	}

	public String toString() {
		String result = "NAME: " + name + "\n" + "IF :" + ifList + "\n"
				+ "ADD:" + addList + "\n" + "DELETE:" + deleteList;
		return result;
	}

	public Vector applyState(Vector theState) {
		for (int i = 0; i < addList.size(); i++) {
			theState.addElement(addList.elementAt(i));
		}
		for (int i = 0; i < deleteList.size(); i++) {
			theState.removeElement(deleteList.elementAt(i));
		}
		return theState;
	}

	// 現状の選ばれたIFリストなどの内容をOperatorで返す
	public Operator getRenamedOperator(int uniqueNum) {
		Vector vars = new Vector();
		// IfListの変数を集める
		for (int i = 0; i < ifList.size(); i++) {
			String anIf = (String) ifList.elementAt(i);
			vars = getVars(anIf, vars);
		}
		// addListの変数を集める
		for (int i = 0; i < addList.size(); i++) {
			String anAdd = (String) addList.elementAt(i);
			vars = getVars(anAdd, vars);
		}
		// deleteListの変数を集める
		for (int i = 0; i < deleteList.size(); i++) {
			String aDelete = (String) deleteList.elementAt(i);
			vars = getVars(aDelete, vars);
		}
		Hashtable renamedVarsTable = makeRenamedVarsTable(vars, uniqueNum);

		// 新しいIfListを作る
		Vector newIfList = new Vector();
		for (int i = 0; i < ifList.size(); i++) {
			String newAnIf = renameVars((String) ifList.elementAt(i),
					renamedVarsTable);
			newIfList.addElement(newAnIf);
		}
		// 新しいaddListを作る
		Vector newAddList = new Vector();
		for (int i = 0; i < addList.size(); i++) {
			String newAnAdd = renameVars((String) addList.elementAt(i),
					renamedVarsTable);
			newAddList.addElement(newAnAdd);
		}
		// 新しいdeleteListを作る
		Vector newDeleteList = new Vector();
		for (int i = 0; i < deleteList.size(); i++) {
			String newADelete = renameVars((String) deleteList.elementAt(i),
					renamedVarsTable);
			newDeleteList.addElement(newADelete);
		}
		// 新しいnameを作る
		String newName = renameVars(name, renamedVarsTable);

		return new Operator(newName, newIfList, newAddList, newDeleteList, isRelatedNextOperator);
	}

	private Vector getVars(String thePattern, Vector vars) {
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				vars.addElement(tmp);
			}
		}
		return vars;
	}

	private Hashtable makeRenamedVarsTable(Vector vars, int uniqueNum) {
		Hashtable result = new Hashtable();
		for (int i = 0; i < vars.size(); i++) {
			String newVar = (String) vars.elementAt(i) + uniqueNum;
			result.put((String) vars.elementAt(i), newVar);
		}
		return result;
	}

	private String renameVars(String thePattern, Hashtable renamedVarsTable) {
		String result = new String();
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				result = result + " " + (String) renamedVarsTable.get(tmp);
			} else {
				result = result + " " + tmp;
			}
		}
		return result.trim();
	}

	public Operator instantiate(Hashtable theBinding) {
		// name を具体化
		String newName = instantiateString(name, theBinding);
		// ifList を具体化
		Vector newIfList = new Vector();
		for (int i = 0; i < ifList.size(); i++) {
			String newIf = instantiateString((String) ifList.elementAt(i),
					theBinding);
			newIfList.addElement(newIf);
		}
		// addList を具体化
		Vector newAddList = new Vector();
		for (int i = 0; i < addList.size(); i++) {
			String newAdd = instantiateString((String) addList.elementAt(i),
					theBinding);
			newAddList.addElement(newAdd);
		}
		// deleteListを具体化
		Vector newDeleteList = new Vector();
		for (int i = 0; i < deleteList.size(); i++) {
			String newDelete = instantiateString(
					(String) deleteList.elementAt(i), theBinding);
			newDeleteList.addElement(newDelete);
		}
		return new Operator(newName, newIfList, newAddList, newDeleteList, isRelatedNextOperator);
	}

	private String instantiateString(String thePattern, Hashtable theBinding) {
		String result = new String();
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				String newString = (String) theBinding.get(tmp);
				if (newString == null) {
					result = result + " " + tmp;
				} else {
					result = result + " " + newString;
				}
			} else {
				result = result + " " + tmp;
			}
		}
		return result.trim();
	}

	private boolean var(String str1) {
		// 先頭が ? なら変数
		return str1.startsWith("?");
	}
}

class Unifier {
	StringTokenizer st1;
	String buffer1[];
	StringTokenizer st2;
	String buffer2[];
	Hashtable vars;

	Unifier() {
		// vars = new Hashtable();
	}

	public boolean unify(String string1,
						 String string2,
						 Hashtable theBindings,
						 boolean isRelatedNextOperator,
						 Operator anOperator,
						 Operator newOperator,
						 Vector newGoals)
	{
		Hashtable orgBindings = new Hashtable();
		for (Enumeration e = theBindings.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String value = (String) theBindings.get(key);
			orgBindings.put(key, value);
		}

		this.vars = theBindings;

		if (unifyToken(string1, string2, theBindings, isRelatedNextOperator) && checkObjectPos(anOperator, newOperator, newGoals, theBindings)) {
			return true;
		} else {
			// 失敗したら元に戻す．
			theBindings.clear();
			for (Enumeration e = orgBindings.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String value = (String) orgBindings.get(key);
				theBindings.put(key, value);
			}
			return false;
		}
	}

	// 関数名変更した
	public boolean unifyToken(String string1, String string2, Hashtable theBindings, boolean isRelatedNextOperator) {
		// 同じなら成功
		if (string1.equals(string2))
			return true;

		// 各々トークンに分ける
		st1 = new StringTokenizer(string1);
		st2 = new StringTokenizer(string2);

		// 数が異なったら失敗
		if (st1.countTokens() != st2.countTokens())
			return false;

		// 定数同士
		int length = st1.countTokens();
		buffer1 = new String[length];
		buffer2 = new String[length];
		for (int i = 0; i < length; i++) {
			buffer1[i] = st1.nextToken();
			buffer2[i] = st2.nextToken();
		}

		// 初期値としてバインディングが与えられていたら
		if (this.vars.size() != 0) {
			for (Enumeration keys = vars.keys(); keys.hasMoreElements();) {
				String key = (String) keys.nextElement();
				String value = (String) vars.get(key);
				replaceBuffer(key, value);
			}
		}

		Integer varTokenCount[] = {0};
		Integer matchPreviousResultCount[] = {0};

		for (int i = 0; i < length; i++) {
			if (!tokenMatching(buffer1[i], buffer2[i], theBindings, varTokenCount, matchPreviousResultCount)) {
				return false;
			}
		}

		if(isRelatedNextOperator && varTokenCount == matchPreviousResultCount){
			return false;
		}

		return true;
	}

	private boolean checkObjectPos(Operator anOperator, Operator newOperator, Vector newGoals, Hashtable theBinding)
	{
		if(anOperator == null || newOperator == null || newGoals == null)
		{
			return true;
		}

		newOperator = anOperator.instantiate(theBinding);
		newGoals = (Vector) newOperator.getIfList();

		for(int goalIndex = 0, deleteCount = 0; goalIndex < newGoals.size(); ++goalIndex)
		{
			StringTokenizer tokens = new StringTokenizer((String)newGoals.get(goalIndex - deleteCount));
			ArrayList<Integer> values = new ArrayList<Integer>();

			while(tokens.hasMoreTokens())
			{
				String token = tokens.nextToken();

				if(token.startsWith("?"))
				{
					values.add((int)theBinding.get(token));
				}
			}

			String funcName = tokens.nextToken();
			if(funcName.indexOf("equal") != -1)
			{
				newGoals.remove(goalIndex - deleteCount);
				deleteCount++;

				if(values.get(0) == values.get(1))
				{
					continue;
				}

				return false;
			}
			else if(funcName.indexOf("dis") != -1)
			{
				newGoals.remove(goalIndex - deleteCount);
				deleteCount++;

				if(values.get(0) - values.get(1) == 1)
				{
					continue;
				}

				return false;
			}
		}

		return true;
	}

	// 課題7-1 変更箇所
	boolean tokenMatching(String token1, String token2, Hashtable theBinding, Integer[] varTokenCount, Integer[] matchPreviousResultCount) {
		if (token1.equals(token2)){
			return true;
		}
		if (var(token1) && !var(token2)){
			++varTokenCount[0];
			return varMatching(token1, token2, theBinding, matchPreviousResultCount);
		}
		if (!var(token1) && var(token2)){
			++varTokenCount[0];
			return varMatching(token2, token1, theBinding, matchPreviousResultCount);
		}
		if (var(token1) && var(token2)){
			return varMatching(token1, token2);
		}
		return false;
	}

	boolean varMatching(String vartoken, String token) {
		if (vars.containsKey(vartoken)) {
			if (token.equals(vars.get(vartoken))) {
				return true;
			} else {
				return false;
			}
		} else {
			replaceBuffer(vartoken, token);
			if (vars.contains(vartoken)) {
				replaceBindings(vartoken, token);
			}
			vars.put(vartoken, token);
		}
		return true;
	}

	boolean varMatching(String vartoken, String token, Hashtable theBinding, Integer[] matchPreviousResultCount) {
		if (vars.containsKey(vartoken)) {
			if (token.equals(vars.get(vartoken))) {
				return true;
			} else {
				return false;
			}
		} else {
			/*
			String uniqueNum = vartoken.replaceAll("[^0-9]", "");
			for (Enumeration e = theBinding.keys(); e.hasMoreElements();) {
				// 同じアサーション内の異なる変数で同じ値が割り当てられた際の処理
				String key = (String) e.nextElement();
				String value = (String) theBinding.get(key);
				if(key.contains(uniqueNum) && value.equals(token)){
					return false;
				}
				else if(key.contains(String.valueOf(Integer.parseInt(uniqueNum) - 1)) && value.equals(token)){
					++matchPreviousResultCount[0];
				}
			}
			replaceBuffer(vartoken, token);
			*/
			if (vars.contains(vartoken)) {
				replaceBindings(vartoken, token);
			}
			vars.put(vartoken, token);
		}
		return true;
	}

	void replaceBuffer(String preString, String postString) {
		for (int i = 0; i < buffer1.length; i++) {
			if (preString.equals(buffer1[i])) {
				buffer1[i] = postString;
			}
			if (preString.equals(buffer2[i])) {
				buffer2[i] = postString;
			}
		}
	}

	void replaceBindings(String preString, String postString) {
		Enumeration keys;
		for (keys = vars.keys(); keys.hasMoreElements();) {
			String key = (String) keys.nextElement();
			if (preString.equals(vars.get(key))) {
				vars.put(key, postString);
			}
		}
	}

	boolean var(String str1) {
		// 先頭が ? なら変数
		return str1.startsWith("?");
	}

}

