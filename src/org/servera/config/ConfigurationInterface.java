package org.servera.config;

import java.util.List;

public interface ConfigurationInterface
{
    //Получить данные ячейки.
    Object getDataPath(String container) throws ConfigException;
    //Получение списка ключей.
    List<String> getKeyDataList(String container) throws ConfigException;
    //Получить путь к конфигу.
    String getPath();
    boolean setDataPath(String container, Object newData) throws ConfigException;
}
