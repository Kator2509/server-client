Система общения между сервером и клиентом. С расширенным функционалом.

## Присутствует внутренний CommandDispatcher.
Можно производить регистрации команд и вызов их обработки. 
Данная схема позволяет общаться между процессом сервера и терминалом.

> CommandDispatcher commandMap = new CommandDispatcher; \
> commandMap.executeCommand(command, arguments);
