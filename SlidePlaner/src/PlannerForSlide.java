import java.io.File;
import java.util.*;

public class PlannerForSlide {
	Vector operators;
	Random rand;
	Vector plan;
	ArrayList<String> process = new ArrayList<String>();
	int stepCount = 0;
	String clearPoint;

	public static void main(String argv[]) {
		(new PlannerForSlide()).start();
	}

	PlannerForSlide() {
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
		operators = new Vector();

		String name1 = new String("Move ?o to the right from ( ?x0 , ?y0 ) to ( ?x1 , ?y1 )");
		 // / IF(座標の差を比べる機構が別で必要)
		Vector ifList1 = new Vector();
		ifList1.addElement(new String("?o at ( ?x0 , ?y0 )"));
		ifList1.addElement(new String("( ?x1 , ?y1 ) is clear"));
		ifList1.addElement(new String("equal( ?y0 , ?y1 )"));
		ifList1.addElement(new String("dis( ?x1 , ?x0 )"));
		// / ADD-LIST
		Vector addList1 = new Vector();
		addList1.addElement(new String("?o at ( ?x1 , ?y1 )"));
		addList1.addElement(new String("( ?x0 , ?y0 ) is clear"));
		// / DELETE-LIST
		Vector deleteList1 = new Vector();
		deleteList1.addElement(new String("?o at ( ?x0 , ?y0 )"));
		deleteList1.addElement(new String("( ?x1 , ?y1 ) is clear"));
		Operator operator1 = new Operator(name1, ifList1, addList1, deleteList1, false);
		operators.addElement(operator1);

		String name2 = new String("Move ?o to the left from ( ?x0 , ?y0 ) to ( ?x1 , ?y1 )");
		 // / IF(座標の差を比べる機構が別で必要)
		Vector ifList2 = new Vector();
		ifList2.addElement(new String("?o at ( ?x0 , ?y0 )"));
		ifList2.addElement(new String("( ?x1 , ?y1 ) is clear"));
		ifList2.addElement(new String("equal( ?y0 , ?y1 )"));
		ifList2.addElement(new String("dis( ?x0 , ?x1 )"));
		// / ADD-LIST
		Vector addList2 = new Vector();
		addList2.addElement(new String("?o at ( ?x1 , ?y1 )"));
		addList2.addElement(new String("( ?x0 , ?y0 ) is clear"));
		// / DELETE-LIST
		Vector deleteList2 = new Vector();
		deleteList2.addElement(new String("?o at ( ?x0 , ?y0 )"));
		deleteList2.addElement(new String("( ?x1 , ?y1 ) is clear"));
		Operator operator2 = new Operator(name2, ifList2, addList2, deleteList2, false);
		operators.addElement(operator2);

		String name3 = new String("Move ?o to the upward from ( ?x0 , ?y0 ) to ( ?x1 , ?y1 )");
		 // / IF(座標の差を比べる機構が別で必要)
		Vector ifList3 = new Vector();
		ifList3.addElement(new String("?o at ( ?x0 , ?y0 )"));
		ifList3.addElement(new String("( ?x1 , ?y1 ) is clear"));
		ifList3.addElement(new String("equal( ?x0 , ?x1 )"));
		ifList3.addElement(new String("dis( ?y0 , ?y1 )"));
		// / ADD-LIST
		Vector addList3 = new Vector();
		addList3.addElement(new String("?o at ( ?x1 , ?y1 )"));
		addList3.addElement(new String("( ?x0 , ?y0 ) is clear"));
		// / DELETE-LIST
		Vector deleteList3 = new Vector();
		deleteList3.addElement(new String("?o at ( ?x0 , ?y0 )"));
		deleteList3.addElement(new String("( ?x1 , ?y1 ) is clear"));
		Operator operator3 = new Operator(name3, ifList3, addList3, deleteList3, false);
		operators.addElement(operator3);

		String name4 = new String("Move ?o to the downward from ( ?x0 , ?y0 ) to ( ?x1 , ?y1 )");
		 // / IF(座標の差を比べる機構が別で必要)
		Vector ifList4 = new Vector();
		ifList4.addElement(new String("?o at ( ?x0 , ?y0 )"));
		ifList4.addElement(new String("( ?x1 , ?y1 ) is clear"));
		ifList4.addElement(new String("equal( ?x0 , ?x1 )"));
		ifList4.addElement(new String("dis( ?y1 , ?y0 )"));
		// / ADD-LIST
		Vector addList4 = new Vector();
		addList4.addElement(new String("?o at ( ?x1 , ?y1 )"));
		addList4.addElement(new String("( ?x0 , ?y0 ) is clear"));
		// / DELETE-LIST
		Vector deleteList4 = new Vector();
		deleteList4.addElement(new String("?o at ( ?x0 , ?y0 )"));
		deleteList4.addElement(new String("( ?x1 , ?y1 ) is clear"));
		Operator operator4 = new Operator(name4, ifList4, addList4, deleteList4, false);
		operators.addElement(operator4);

		/*
		// OPERATOR 1
		// / NAME
		String name1 = new String("Place ?x on ?y");
		// / IF
		Vector ifList1 = new Vector();
		ifList1.addElement(new String("clear ?y"));
		ifList1.addElement(new String("holding ?x"));
		// / ADD-LIST
		Vector addList1 = new Vector();
		addList1.addElement(new String("?x on ?y"));
		addList1.addElement(new String("clear ?x"));
		addList1.addElement(new String("handEmpty"));
		// / DELETE-LIST
		Vector deleteList1 = new Vector();
		deleteList1.addElement(new String("clear ?y"));
		deleteList1.addElement(new String("holding ?x"));
		Operator operator1 = new Operator(name1, ifList1, addList1, deleteList1, false);
		operators.addElement(operator1);

		// OPERATOR 2
		// / NAME
		String name2 = new String("remove ?x from on top ?y");
		// / IF
		Vector ifList2 = new Vector();
		ifList2.addElement(new String("?x on ?y"));
		ifList2.addElement(new String("clear ?x"));
		ifList2.addElement(new String("handEmpty"));
		// / ADD-LIST
		Vector addList2 = new Vector();
		addList2.addElement(new String("clear ?y"));
		addList2.addElement(new String("holding ?x"));
		// / DELETE-LIST
		Vector deleteList2 = new Vector();
		deleteList2.addElement(new String("?x on ?y"));
		deleteList2.addElement(new String("clear ?x"));
		deleteList2.addElement(new String("handEmpty"));
		Operator operator2 = new Operator(name2, ifList2, addList2, deleteList2, true);
		operators.addElement(operator2);

		// OPERATOR 3
		// / NAME
		String name3 = new String("pick up ?x from the table");
		// / IF
		Vector ifList3 = new Vector();
		ifList3.addElement(new String("ontable ?x"));
		ifList3.addElement(new String("clear ?x"));
		ifList3.addElement(new String("handEmpty"));
		// / ADD-LIST
		Vector addList3 = new Vector();
		addList3.addElement(new String("holding ?x"));
		// / DELETE-LIST
		Vector deleteList3 = new Vector();
		deleteList3.addElement(new String("ontable ?x"));
		deleteList3.addElement(new String("clear ?x"));
		deleteList3.addElement(new String("handEmpty"));
		Operator operator3 = new Operator(name3, ifList3, addList3, deleteList3, true);
		operators.addElement(operator3);

		// OPERATOR 4
		// / NAME
		String name4 = new String("put ?x down on the table");
		// / IF
		Vector ifList4 = new Vector();
		ifList4.addElement(new String("holding ?x"));
		// / ADD-LIST
		Vector addList4 = new Vector();
		addList4.addElement(new String("ontable ?x"));
		addList4.addElement(new String("clear ?x"));
		addList4.addElement(new String("handEmpty"));
		// / DELETE-LIST
		Vector deleteList4 = new Vector();
		deleteList4.addElement(new String("holding ?x"));
		Operator operator4 = new Operator(name4, ifList4, addList4, deleteList4, false);
		operators.addElement(operator4);

		// 対になるオペレータを登録
		operator1.addPairedOperator(operator3);
		operator1.addPairedOperator(operator4);

		operator2.addPairedOperator(operator3);
		operator2.addPairedOperator(operator4);

		operator3.addPairedOperator(operator1);
		operator3.addPairedOperator(operator2);

		operator4.addPairedOperator(operator1);
		operator4.addPairedOperator(operator2);
		*/
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
