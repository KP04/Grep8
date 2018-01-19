import java.util.ArrayList;


public class SlidePlaner_Pre {

	ArrayList<String> operators;
	ArrayList<String> plan;

	public static void main(String argv[])
	{
		(new SlidePlaner_Pre()).start();
	}

	public void start()
	{
		initOperators();
		ArrayList<String> goalList = initGoalList();
		ArrayList<String> stateList = initStateList();
	}

	private boolean planning()
	{
		return false;
	}

	private void initOperators()
	{

	}

	private ArrayList<String> initStateList()
	{
		ArrayList<String> stateList = new ArrayList<String>();

		stateList.add("1 at ( 1 , 0 )");
		stateList.add("2 at ( 1 , 1 )");
		stateList.add("3 at ( 2 , 0 )");
		stateList.add("4 at ( 0 , 1 )");
		stateList.add("5 at ( 1 , 2 )");
		stateList.add("6 at ( 2 , 1 )");
		stateList.add("7 at ( 0 , 2 )");
		stateList.add("8 at ( 2 , 2 )");
		stateList.add("( 0 , 0 ) is clear");

		return stateList;
	}

	private ArrayList<String> initGoalList()
	{
		ArrayList<String> goalList = new ArrayList<String>();

		goalList.add("1 at ( 0 , 0 )");
		goalList.add("2 at ( 1 , 0 )");
		goalList.add("3 at ( 2 , 0 )");
		goalList.add("4 at ( 0 , 1 )");
		goalList.add("5 at ( 1 , 1 )");
		goalList.add("6 at ( 2 , 1 )");
		goalList.add("7 at ( 0 , 2 )");
		goalList.add("8 at ( 1 , 2 )");
		goalList.add("( 2 , 2 ) is clear");

		return goalList;
	}
}
