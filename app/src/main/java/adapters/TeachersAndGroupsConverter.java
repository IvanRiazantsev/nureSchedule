package adapters;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


import androidx.room.TypeConverter;

public class TeachersAndGroupsConverter {

    @TypeConverter
    public String from(List<Integer> list) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next());
            if (iterator.hasNext()) {
                stringBuilder.append(",");
            }
        }
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
