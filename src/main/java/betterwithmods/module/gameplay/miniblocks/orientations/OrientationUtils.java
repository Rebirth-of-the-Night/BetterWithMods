package betterwithmods.module.gameplay.miniblocks.orientations;

public class OrientationUtils {
    public static float CENTER_OFFSET = 0.5F;

    public static int getCorner(double centerA, double centerB, double cornerSize) {
        boolean posA = centerA > 0, posB = centerB > 0;
        double x = Math.abs(centerA), z = Math.abs(centerB);
        if (x > cornerSize && z > cornerSize) {
            if (posA && posB)
                return 0;
            else if (posA)
                return 1;
            else if (!posB)
                return 2;
            else
                return 3;
        }
        return -1;
    }

    public static int getCorner(double centerA, double centerB) {
        return getCorner(centerA, centerB, 0.25);
    }

    public static boolean isMax(double hit1, double hit2) {
        return Math.max(Math.abs(hit1), Math.abs(hit2)) == Math.abs(hit1);
    }

    public static boolean inCenter(float hit1, float hit2, float max) {
        return Math.abs(hit1) <= max && Math.abs(hit2) <= max;
    }

}
