package jgreg.internship.nii.Utils;

import java.util.List;
import java.util.regex.Pattern;

public class Utils {

    public static Pattern PatternFactory(List<String> list) {
        StringBuilder acc = new StringBuilder();
        if (list.size() > 0) {
            String last = list.remove(list.size() - 1);
            
            for (String s : list) {
                acc.append(s)
                        .append('|');
            }
            
            acc.append(last);
        }

        return Pattern.compile(acc.toString());
    }
}
