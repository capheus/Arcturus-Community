package com.eu.habbo.habbohotel.catalog;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Voucher
{

    public final int id;


    public final String code;


    public final int credits;


    public final int points;


    public final int pointsType;


    public final int catalogItemId;


    public Voucher(ResultSet set) throws SQLException
    {
        this.id            = set.getInt("id");
        this.code          = set.getString("code");
        this.credits       = set.getInt("credits");
        this.points        = set.getInt("points");
        this.pointsType    = set.getInt("points_type");
        this.catalogItemId = set.getInt("catalog_item_id");
    }
}
