# Система общения между сервером и клиентом. С расширенным функционалом.

## Присутствует внутренний CommandDispatcher.
Можно производить регистрации команд и вызов их обработки. 
Данная схема позволяет общаться между процессом сервера и терминалом.

> CommandDispatcher commandMap = new CommandDispatcher(); \
> commandMap.executeCommand(command, arguments);
 
### Вы так же можете передать в CommandDispatcher свой Map, если не хотите использовать встроенный.

## Система использует драйвер JDBC для общения с БД.

### Создается объект подключения, который можно будет открывать или закрывать в необходимые для программы моменты.

> public class UserDataBase extends Connector
> {\
>   public UserDataBase()\
>   {\
>       super(login, password, address, port)    
>   }
> 
>   public createConnection()\
>   {\
>       YOUR CODE HERE\
>   }\
> }

## Присутствует внутренняя система прав UserManager

> UserManager manager = new UserManager(); \
> manager.createUser();

## В системе имеется менеджер файлов.

### По желанию, вы можете его использовать для создания файлов, которые будут использоваться в вашей системе для хранения каких-либо данных.

> Manager fileManager = new Manager(); \
> fileManager.createSystemDirectory(path); \
> fileManager.createSystemFile(pathWithFile.formatFile);