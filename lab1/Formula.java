import java.util.List;

public class Formula {
    public static void main(String[] args) {
        int[] n = new int[11];
        for (int num = 16; num >= 6; num--) {
            n[num * -1 + 16] = num;
        }
        float[] x = new float[18];
        for (int num = 0; num <= 17; num++) {
            x[num] = (float) Math.random() * 23 - 8;
        }
        float[][] w = new float[11][18];
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 18; j++) {
                w[i][j] = calculate(n[i], x[j]);
            }
        }
        print_matrix(w);
    }

    public static float calculate(int cond, float number) {
        if (cond == 7) {
            return (float) Math.cbrt(Math.atan(Math.sin(number)));
        } else if (List.of(8, 9, 10, 13, 16).contains(cond)) {
            return (float) Math.tan(Math.pow(Math.E, Math.atan((number + 3.5) / 23)));
        } else {
            return (float) Math.tan(Math.tan(Math.sin(Math.pow(number / (number - 1), 2))));
        }
    }

    public static void print_matrix(float[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            // String[] components = new String[matrix[i].length];
            // for (int j = 0; j < matrix[i].length; j++) {
            //     components[j] = String.format("%.5f", matrix[i][j]);
            // }
            // System.out.println(String.join(" ", components));
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(String.format("%.5f ", matrix[i][j]));
            }
        System.out.println();
        }
    }
}
