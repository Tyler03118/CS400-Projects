public class Main {

    public static void main(String[] args) {
	System.out.println("FIXME: Do something with the MyList class");
	MyList<Integer> testList = new MyList<>();
	testList.add(1);
	testList.add(2);
	testList.add(3);
	int secNum = testList.get(1);
	System.out.println("The second number is " + secNum);
    }

}
