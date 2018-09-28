package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionTent extends InteractionDefault
{
    public InteractionTent(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionTent(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }
}
