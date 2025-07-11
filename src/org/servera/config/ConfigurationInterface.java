package org.servera.config;

public interface ConfigurationInterface
{
    //Получить данные ячейки.
    Object getDataPath(String container);
    Object getKeyData(String container);
    //Получить путь к конфигу.
    String getPath();
}
