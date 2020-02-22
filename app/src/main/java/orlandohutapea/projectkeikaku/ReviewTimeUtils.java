package orlandohutapea.projectkeikaku;

import android.util.SparseIntArray;

import java.util.Calendar;

public class ReviewTimeUtils {
    static final SparseIntArray boxToIntervalType = new SparseIntArray() {
        {
            append(1, Calendar.MINUTE);
            append(2, Calendar.MINUTE);
            append(3, Calendar.HOUR_OF_DAY);
            append(4, Calendar.HOUR_OF_DAY);
            append(5, Calendar.DAY_OF_YEAR);
            append(6, Calendar.DAY_OF_YEAR);
            append(7, Calendar.DAY_OF_YEAR);
            append(8, Calendar.MONTH);
            append(9, Calendar.YEAR);
        }
    };
    static final SparseIntArray boxToIntervalValue = new SparseIntArray() {
        {
            append(1, 2);
            append(2, 10);
            append(3, 1);
            append(4, 5);
            append(5, 1);
            append(6, 5);
            append(7, 25);
            append(8, 4);
            append(9, 2);
        }
    };
}
