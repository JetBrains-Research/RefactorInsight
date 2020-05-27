package main.java.vehicles.components;

public class Utils {

    private static boolean z;

    public static void parseString(String string) {
        Result.abc = string.toLowerCase().contains("abc");
        boolean abc = Result.abc;
        Result.aBoolean = string.lastIndexOf(".") > 5;
        boolean b = Result.aBoolean;
        Utils.z = string.endsWith("z");
        boolean z = Utils.z;
        boolean result = abc && b && z;
        if (result) {
            string = string.replace("x.x", "");
        } else {
            return;
        }
    }

    public static void print(int height, int width, double perimeter) {
        System.out.println(perimeter);
        final double area = height * width;
        System.out.println(area);
    }

}
