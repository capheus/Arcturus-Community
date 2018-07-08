package com.eu.habbo.habbohotel.achievements;

import gnu.trove.map.hash.THashMap;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Achievement
{

    public final int id;


    public final String name;


    public final AchievementCategories category;


    public final THashMap<Integer, AchievementLevel> levels;


    public Achievement(ResultSet set) throws SQLException
    {
        levels = new THashMap<Integer, AchievementLevel>();

        id = set.getInt("id");
        this.name = set.getString("name");
        this.category = AchievementCategories.valueOf(set.getString("category").toUpperCase());

        this.addLevel(new AchievementLevel(set));
    }


    public void addLevel(AchievementLevel level)
    {
        synchronized (this.levels)
        {
            this.levels.put(level.level, level);
        }
    }


    public AchievementLevel getLevelForProgress(int progress)
    {
        AchievementLevel l = null;

        for(AchievementLevel level : this.levels.values())
        {
            if (l == null && level.level == 1)
            {
                l = level;
            }

            if (progress >= level.progress)
            {
                if (l != null)
                {
                    if (l.level > level.level)
                    {
                        continue;
                    }
                }

                l = level;
            }
        }

        return l;
    }


    public AchievementLevel getNextLevel(int currentLevel)
    {
        AchievementLevel l = null;

        for(AchievementLevel level : this.levels.values())
        {
            if(level.level == (currentLevel + 1))
                return level;
        }

        return null;
    }
}
