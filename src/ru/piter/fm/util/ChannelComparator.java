package ru.piter.fm.util;

import ru.piter.fm.radio.Channel;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 12.10.2010
 * Time: 3:05:28
 * To change this template use File | Settings | File Templates.
 */
public class ChannelComparator implements Comparator {

    private static String sortBy = "range";

    public ChannelComparator(String sortBy) {
        this.sortBy = sortBy;
    }

    public int compare(Object o1, Object o2) {
        Channel channel_1 = (Channel) o1;
        Channel channel_2 = (Channel) o2;

        if (sortBy.equals("sort_by_range")) {

            float currentValue = Float.parseFloat(channel_1.getRange().split("\\s")[0]);
            float newValue = Float.parseFloat(channel_2.getRange().split("\\s")[0]);
            
            if (currentValue < newValue)
                return -1;
            else if (currentValue > newValue)
                return 1;
            return 0;

        } else if (sortBy.equals("sort_by_name")) {
              return channel_1.getName().compareTo(channel_2.getName());
        }
        return 0;
    }
}
