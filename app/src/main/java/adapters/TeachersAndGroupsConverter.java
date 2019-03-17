package adapters;

import java.util.Arrays;
import java.util.List;


import androidx.room.TypeConverter;

public class TeachersAndGroupsConverter {

    @TypeConverter
    public String from(List<Integer> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer integer : list) {
            stringBuilder.append(integer).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    @TypeConverter
    public List<Integer> to(String data) {
        String[] strings = data.split(",");
        Integer[] integers = new Integer[strings.length];
        for (int i = 0; i < integers.length; i++) {
            integers[i] = Integer.parseInt(strings[i]);
        }
        return Arrays.asList(integers);
    }
}
