package com.eu.habbo.core;

import com.eu.habbo.Emulator;
import com.eu.habbo.plugin.events.emulator.EmulatorConfigUpdatedEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Map;
import java.util.Properties;

public class ConfigurationManager
{

    public boolean loaded = false;


    public boolean isLoading = false;


    private final Properties properties;

    private final String configurationPath;
    
    public ConfigurationManager(String configurationPath) throws Exception
    {
        this.properties = new Properties();
        this.configurationPath = configurationPath;
        this.reload();
    }


    public void reload()
    {
        this.isLoading = true;
        this.properties.clear();

        InputStream input = null;

        try
        {
            File f = new File(this.configurationPath);
            input = new FileInputStream(f);
            this.properties.load(input);

        }
        catch (IOException ex)
        {
            Emulator.getLogging().logErrorLine("[CRITICAL] FAILED TO LOAD CONFIG FILE! (" + this.configurationPath + ")");
            ex.printStackTrace();
        }
        finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        if(this.loaded)
        {
            this.loadFromDatabase();
        }

        this.isLoading = false;
        Emulator.getLogging().logStart("Configuration Manager -> Loaded!");

        if (Emulator.getPluginManager() != null)
        {
            Emulator.getPluginManager().fireEvent(new EmulatorConfigUpdatedEvent());
        }
    }


    public void loadFromDatabase()
    {
        Emulator.getLogging().logStart("Loading configuration from database...");

        long millis = System.currentTimeMillis();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement())
        {
            if (statement.execute("SELECT * FROM emulator_settings"))
            {
                try (ResultSet set = statement.getResultSet())
                {
                    while (set.next())
                    {
                        this.properties.put(set.getString("key"), set.getString("value"));
                    }
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        Emulator.getLogging().logStart("Configuration -> loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void saveToDatabase()
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE emulator_settings SET `value` = ? WHERE `key` = ? LIMIT 1"))
        {
            for (Map.Entry<Object, Object> entry : this.properties.entrySet())
            {
                statement.setString(1, entry.getValue().toString());
                statement.setString(2, entry.getKey().toString());
                statement.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }


    public String getValue(String key)
    {
        return this.getValue(key, "");
    }


    public String getValue(String key, String defaultValue)
    {
        if (this.isLoading)
            return defaultValue;

        if (!this.properties.containsKey(key)) {
            Emulator.getLogging().logErrorLine("[CONFIG] Key not found: " + key);
        }
        return this.properties.getProperty(key, defaultValue);
    }


    public boolean getBoolean(String key)
    {
        return this.getBoolean(key, false);
    }


    public boolean getBoolean(String key, boolean defaultValue)
    {
        if (this.isLoading)
            return defaultValue;

        try
        {
            return (this.getValue(key, "0").equals("1")) || (this.getValue(key, "false").equals("true"));
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine("Failed to parse key " + key + " with value " + this.getValue(key) + " to type boolean.");
        }
        return defaultValue;
    }


    public int getInt(String key)
    {
        return this.getInt(key, 0);
    }


    public int getInt(String key, Integer defaultValue)
    {
        if (this.isLoading)
            return defaultValue;

        try
        {
            return Integer.parseInt(this.getValue(key, defaultValue.toString()));
        } catch (Exception e)
        {
            Emulator.getLogging().logErrorLine("Failed to parse key " + key + " with value " + this.getValue(key) + " to type integer.");
        }
        return defaultValue;
    }


    public double getDouble(String key)
    {
        return this.getDouble(key, 0.0);
    }


    public double getDouble(String key, Double defaultValue)
    {
        if (this.isLoading)
            return defaultValue;

        try
        {
            return Double.parseDouble(this.getValue(key, defaultValue.toString()));
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine("Failed to parse key " + key + " with value " + this.getValue(key) + " to type double.");
        }

        return defaultValue;
    }


    public void update(String key, String value)
    {
        this.properties.setProperty(key, value);
    }

    public void register(String key, String value)
    {
        if (this.properties.getProperty(key, null) != null)
            return;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO emulator_settings VALUES (?, ?)"))
        {
            statement.setString(1, key);
            statement.setString(2, value);
            statement.execute();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        this.update(key, value);
    }
}