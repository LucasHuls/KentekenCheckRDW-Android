package nl.lucashuls.kentekencheck.utils;

import android.app.DatePickerDialog;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerUtil {

    public static void showDatePickerDialog(Context context, String initialDate, DatePickerCallback callback) {
        Calendar cal = Calendar.getInstance();
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(initialDate);
            cal.setTime(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, monthOfYear, dayOfMonth);
                    Date date = selectedDate.getTime();
                    String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
                    callback.onDateSelected(formattedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    // Callback interface for the date picker dialog
    public interface DatePickerCallback {
        void onDateSelected(String date);
    }
}
