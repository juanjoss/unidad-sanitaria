package util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import ui.MainFrame;

public class DateUtil {

    /**
     * Transforma un @String de formato fecha fromFormat a toFormat.
     *
     * @param date A {@code String} fecha a formatear.
     * @param fromFormat A {@code String} formato original.
     * @param toFormat A {@code String} formato final.
     * @return A {@code String} fecha en formato toFormat.
     */
    public static String formatDate(String date, String fromFormat, String toFormat) {
        DateFormat originalFormat = new SimpleDateFormat(fromFormat);
        DateFormat targetFormat = new SimpleDateFormat(toFormat);

        String formattedDate = "";
        try {
            Date originalDate = originalFormat.parse(date);
            formattedDate = targetFormat.format(originalDate);
        } catch (ParseException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        return formattedDate;
    }

    /**
     * Valida una fecha dada como A {@code String} y con un formato.
     *
     * @param date A {@code String} fecha a validar.
     * @param format A {@code String} formato a validar.
     * @return A {@code boolean} true o false.
     */
    public static boolean isValidDate(String date, String format) {
        boolean valid;

        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern(format)
                    .withResolverStyle(ResolverStyle.STRICT)
            );
            valid = true;
        } catch (DateTimeParseException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            valid = false;
        }

        return valid;
    }
}
