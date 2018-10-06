package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.items.Item;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectMatchFurniStaff extends WiredEffectMatchFurni
{
    public WiredEffectMatchFurniStaff(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
        this.checkForWiredResetPermission = false;
    }

    public WiredEffectMatchFurniStaff(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.checkForWiredResetPermission = false;
    }
}
