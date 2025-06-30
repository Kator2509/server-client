Очень простой пример использования системы Client-Server на языке Java версии 21.
Основные поля это Socket и ServerSocket.


При желании можно адаптировать как простой мессенджер между клиентом и сервером при использовании объектов DataOutputStream и DataInputStream.

Присутствует внутренний CommandDispatcher. 
Можно производить регистрации команд и вызов их обработки. Данная схема позволяет общаться между процессом сервера и пользователем.

update: CommandDispatcher commandMap = new CommandDispatcher;
commandMap.executeCommand(command, arguments);
